package com.neusoft.yyzx.service;

import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.Bed;

import java.util.List;

/**
 * 床位业务接口：示意图查询、调换
 */
public interface BedService {
    /**
     * 床位示意图：查询全部床位
     * @return 床位列表
     */
    public List<Bed> listAllBeds();

    /**
     * 入住/调换时联动获取空闲床位
     * @param roomNo 房间号
     * @return 空闲床位列表
     */
    public List<Bed> listFreeBeds(int roomNo);

    /**
     * 床位调换流程（对应项目结构 六.4）
     * @param customerId 客户ID
     * @param newBedId 新床位ID
     * @return 调换结果
     */
    public Result<Void> swapBed(int customerId, int newBedId);

    /**
     * 床位管理：多条件查询（房间号/床位号/状态，null 忽略）
     * @param roomNo 房间号
     * @param bedNo 床位号
     * @param status 床位状态
     * @return 床位列表
     */
    public List<Bed> listByCondition(Integer roomNo, String bedNo, Integer status);

    /**
     * 根据ID查询床位
     * @param id 床位ID
     * @return 床位对象，不存在返回null
     */
    public Bed findById(int id);

    /**
     * 床位管理：新增床位（默认状态为空闲）
     * @param bed 床位对象
     * @return 新增结果（data为床位ID）
     */
    public Result<Integer> addBed(Bed bed);

    /**
     * 床位管理：修改床位信息（床位号/状态/备注）
     * @param bed 床位对象
     * @return 修改结果
     */
    public Result<Void> updateBed(Bed bed);

    /**
     * 床位管理：删除床位（仅空闲床位可删除）
     * @param id 床位ID
     * @return 删除结果
     */
    public Result<Void> deleteBed(int id);
}
