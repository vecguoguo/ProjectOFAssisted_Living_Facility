package com.neusoft.yyzx.service.impl;

import com.neusoft.yyzx.common.constant.BedStatusConstant;
import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.common.result.ResultCode;
import com.neusoft.yyzx.dao.impl.BedDAOImpl;
import com.neusoft.yyzx.dao.impl.BedDetailsDAOImpl;
import com.neusoft.yyzx.dao.impl.CustomerDAOImpl;
import com.neusoft.yyzx.db.TxManager;
import com.neusoft.yyzx.entity.Bed;
import com.neusoft.yyzx.entity.BedDetails;
import com.neusoft.yyzx.entity.Customer;
import com.neusoft.yyzx.service.BedService;

import java.time.LocalDate;
import java.util.List;

public class BedServiceImpl implements BedService {

    @Override
    public List<Bed> listAllBeds() {
        return TxManager.query(conn -> {
            BedDAOImpl bedDAO = new BedDAOImpl();
            bedDAO.setConnection(conn);
            return bedDAO.listAll();
        });
    }

    @Override
    public List<Bed> listFreeBeds(int roomNo) {
        return TxManager.query(conn -> {
            BedDAOImpl bedDAO = new BedDAOImpl();
            bedDAO.setConnection(conn);
            return bedDAO.listFreeByRoomNo(roomNo, BedStatusConstant.FREE);
        });
    }

    @Override
    public List<Bed> listByCondition(Integer roomNo, String bedNo, Integer status) {
        return TxManager.query(conn -> {
            BedDAOImpl bedDAO = new BedDAOImpl();
            bedDAO.setConnection(conn);
            return bedDAO.listByCondition(roomNo, bedNo, status);
        });
    }

    @Override
    public Bed findById(int id) {
        return TxManager.query(conn -> {
            BedDAOImpl bedDAO = new BedDAOImpl();
            bedDAO.setConnection(conn);
            return bedDAO.findById(id);
        });
    }

    /**
     * 新增床位：同一房间床位号不可重复，默认状态为空闲
     */
    @Override
    public Result<Integer> addBed(Bed bed) {
        return TxManager.tx(conn -> {
            if (bed.getRoomNo() == null || bed.getBedNo() == null || bed.getBedNo().isBlank()) {
                return Result.fail(ResultCode.PARAM_ERROR);
            }
            BedDAOImpl bedDAO = new BedDAOImpl();
            bedDAO.setConnection(conn);
            if (bedDAO.findByRoomNoAndBedNo(bed.getRoomNo(), bed.getBedNo()) != null) {
                return Result.fail(ResultCode.BED_NO_EXISTS);
            }
            if (bed.getBedStatus() == null) {
                bed.setBedStatus(BedStatusConstant.FREE);
            }
            int id = bedDAO.insert(bed);
            return Result.success(id);
        });
    }

    /**
     * 修改床位：床位存在；若修改房间号+床位号，需与其他床位不冲突
     */
    @Override
    public Result<Void> updateBed(Bed bed) {
        return TxManager.tx(conn -> {
            BedDAOImpl bedDAO = new BedDAOImpl();
            bedDAO.setConnection(conn);
            Bed exist = bedDAO.findById(bed.getId());
            if (exist == null) {
                return Result.fail(ResultCode.BED_NOT_FOUND);
            }
            Bed sameNo = bedDAO.findByRoomNoAndBedNo(bed.getRoomNo(), bed.getBedNo());
            if (sameNo != null && !sameNo.getId().equals(bed.getId())) {
                return Result.fail(ResultCode.BED_NO_EXISTS);
            }
            if (bed.getBedStatus() == null) {
                bed.setBedStatus(exist.getBedStatus());
            }
            bedDAO.updateBed(bed);
            return Result.success();
        });
    }

    /**
     * 删除床位：仅空闲床位可删除
     */
    @Override
    public Result<Void> deleteBed(int id) {
        return TxManager.tx(conn -> {
            BedDAOImpl bedDAO = new BedDAOImpl();
            bedDAO.setConnection(conn);
            Bed bed = bedDAO.findById(id);
            if (bed == null) {
                return Result.fail(ResultCode.BED_NOT_FOUND);
            }
            if (!BedStatusConstant.FREE.equals(bed.getBedStatus())) {
                return Result.fail(ResultCode.BED_OCCUPIED_UNDELETABLE);
            }
            bedDAO.deleteById(id);
            return Result.success();
        });
    }

    /**
     * 床位调换流程（对应项目结构 六.4）：
     * 选择空闲床位 -> 旧床位详情失效(结束时间=当天) -> 新增新床位详情(入住时间=当天)
     * -> 旧床位改空闲、新床位改有人 -> 更新客户房间号、床位号、楼号
     */
    @Override
    public Result<Void> swapBed(int customerId, int newBedId) {
        return TxManager.tx(conn -> {
            CustomerDAOImpl customerDAO = new CustomerDAOImpl();
            BedDAOImpl bedDAO = new BedDAOImpl();
            BedDetailsDAOImpl bedDetailsDAO = new BedDetailsDAOImpl();
            customerDAO.setConnection(conn);
            bedDAO.setConnection(conn);
            bedDetailsDAO.setConnection(conn);

            Customer customer = customerDAO.findById(customerId);
            // 加行锁读取目标床位：锁持有至事务提交，避免并发把同一张空闲床分给两个客户
            Bed newBed = bedDAO.findByIdForUpdate(newBedId);
            if (customer == null) {
                return Result.fail(ResultCode.USER_NOT_FOUND);
            }
            if (newBed == null || !BedStatusConstant.FREE.equals(newBed.getBedStatus())) {
                return Result.fail(ResultCode.BED_SWAP_INVALID);
            }
            // 防御校验：目标床若已有有效占用记录（beddetails 未失效），即便 bed_status 因数据漂移
            // 被误标为空闲，也必须拒绝，避免一张床被两个客户同时占用（双重占用）。
            if (!bedDetailsDAO.listByBedId(newBedId).isEmpty()) {
                return Result.fail(ResultCode.BED_SWAP_INVALID);
            }
            LocalDate today = LocalDate.now();

            // 1. 旧床位详情失效
            BedDetails active = bedDetailsDAO.findActiveByCustomerId(customerId);
            int oldBedId = customer.getBedId();
            if (active != null) {
                bedDetailsDAO.expire(active.getId(), today);
            }
            // 2. 新增新床位详情
            BedDetails fresh = new BedDetails();
            fresh.setStartDate(today);
            fresh.setEndDate(null);
            fresh.setBedDetails("调换入住-床位" + newBed.getBedNo());
            fresh.setCustomerId(customerId);
            fresh.setBedId(newBedId);
            bedDetailsDAO.insert(fresh);
            // 3. 旧床位空闲、新床位有人
            bedDAO.updateStatus(oldBedId, BedStatusConstant.FREE);
            bedDAO.updateStatus(newBedId, BedStatusConstant.OCCUPIED);
            // 4. 更新客户床位信息
            customerDAO.updateBedInfo(customerId, String.valueOf(newBed.getRoomNo()), newBedId,
                    customer.getBuildingNo());
            return Result.success();
        });
    }
}
