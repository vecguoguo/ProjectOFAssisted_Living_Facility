package com.neusoft.yyzx.dao;

import com.neusoft.yyzx.entity.Bed;

import java.util.List;

/**
 * bed 表数据访问接口
 */
public interface BedDAO {
    /**
     * 根据ID查询床位
     * @param id 床位ID
     * @return 床位对象，不存在返回null
     */
    public Bed findById(int id);

    /**
     * 加行锁查询床位（SELECT ... FOR UPDATE），仅应在事务内使用。
     * 用于入住/换床等“读-改-写”分配场景，锁持有至事务提交，
     * 防止两个并发请求同时抢到同一张空闲床位（双重占用）。
     * @param id 床位ID
     * @return 床位对象，不存在返回null
     */
    public Bed findByIdForUpdate(int id);

    /**
     * 按房间号查询床位（床位示意图/联动选择空闲床位）
     * @param roomNo 房间号
     * @return 床位列表
     */
    public List<Bed> listByRoomNo(int roomNo);

    /**
     * 查询指定房间的空闲床位
     * @param roomNo 房间号
     * @param freeStatus 空闲状态值
     * @return 床位列表
     */
    public List<Bed> listFreeByRoomNo(int roomNo, int freeStatus);

    /**
     * 查询全部床位
     * @return 床位列表
     */
    public List<Bed> listAll();

    /**
     * 更新床位状态
     * @param id 床位ID
     * @param status 目标状态
     * @return 影响行数
     */
    public int updateStatus(int id, int status);

    /**
     * 多条件查询床位（床位管理：房间号/床位号/状态任意组合，null 表示忽略该条件）
     * @param roomNo 房间号
     * @param bedNo 床位号
     * @param status 床位状态
     * @return 床位列表
     */
    public List<Bed> listByCondition(Integer roomNo, String bedNo, Integer status);

    /**
     * 按房间号+床位号查询床位（新增时查重）
     * @param roomNo 房间号
     * @param bedNo 床位号
     * @return 床位对象，不存在返回null
     */
    public Bed findByRoomNoAndBedNo(int roomNo, String bedNo);

    /**
     * 新增床位，返回自增主键
     * @param bed 床位对象
     * @return 自增主键
     */
    public int insert(Bed bed);

    /**
     * 修改床位信息（床位号、备注、状态）
     * @param bed 床位对象
     * @return 影响行数
     */
    public int updateBed(Bed bed);

    /**
     * 物理删除床位（bed 表无逻辑删除标记）
     * @param id 床位ID
     * @return 影响行数
     */
    public int deleteById(int id);
}
