package com.neusoft.yyzx;

import com.neusoft.yyzx.common.constant.AuditStatusConstant;
import com.neusoft.yyzx.common.constant.BedStatusConstant;
import com.neusoft.yyzx.common.constant.RetreatTypeConstant;
import com.neusoft.yyzx.common.constant.NurseStatusConstant;
import com.neusoft.yyzx.common.constant.RoleConstant;
import com.neusoft.yyzx.common.constant.ServiceStatusConstant;
import com.neusoft.yyzx.common.result.Result;
import com.neusoft.yyzx.entity.Backdown;
import com.neusoft.yyzx.entity.Bed;
import com.neusoft.yyzx.entity.Customer;
import com.neusoft.yyzx.entity.Outward;
import com.neusoft.yyzx.entity.CustomerNurseItem;
import com.neusoft.yyzx.entity.NurseContent;
import com.neusoft.yyzx.entity.NurseLevel;
import com.neusoft.yyzx.entity.NurseRecord;
import com.neusoft.yyzx.entity.User;
import com.neusoft.yyzx.service.BackdownService;
import com.neusoft.yyzx.service.BedService;
import com.neusoft.yyzx.service.CustomerNurseService;
import com.neusoft.yyzx.service.CustomerService;
import com.neusoft.yyzx.service.LoginService;
import com.neusoft.yyzx.service.NurseContentService;
import com.neusoft.yyzx.service.NurseLevelService;
import com.neusoft.yyzx.service.NurseRecordService;
import com.neusoft.yyzx.service.OutwardService;
import com.neusoft.yyzx.service.UserService;
import com.neusoft.yyzx.service.impl.BackdownServiceImpl;
import com.neusoft.yyzx.service.impl.BedServiceImpl;
import com.neusoft.yyzx.service.impl.CustomerNurseServiceImpl;
import com.neusoft.yyzx.service.impl.CustomerServiceImpl;
import com.neusoft.yyzx.service.impl.LoginServiceImpl;
import com.neusoft.yyzx.service.impl.NurseContentServiceImpl;
import com.neusoft.yyzx.service.impl.NurseLevelServiceImpl;
import com.neusoft.yyzx.service.impl.NurseRecordServiceImpl;
import com.neusoft.yyzx.service.impl.OutwardServiceImpl;
import com.neusoft.yyzx.service.impl.UserServiceImpl;
import com.neusoft.yyzx.util.ConsoleUtil;
import com.neusoft.yyzx.view.Session;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 东软颐养中心（JavaSE 控制台版）入口
 *
 * 模块划分与《项目结构.md》一致，当前已实现：
 * 登录认证 / 客户入住登记 / 床位示意图 / 床位调换 / 床位管理 / 用户管理 / 护理管理（管理员）/ 日常护理（健康管家）
 * 客户管理：退住登记（申请+审核）、外出登记（申请+审核+回院登记）
 * 其余模块（膳食等）按相同 DAO+Service 模式扩展
 */
public class Main {

    private static final LoginService loginService = new LoginServiceImpl();
    private static final CustomerService customerService = new CustomerServiceImpl();
    private static final BedService bedService = new BedServiceImpl();
    private static final NurseLevelService nurseLevelService = new NurseLevelServiceImpl();
    private static final NurseContentService nurseContentService = new NurseContentServiceImpl();
    private static final CustomerNurseService customerNurseService = new CustomerNurseServiceImpl();
    private static final NurseRecordService nurseRecordService = new NurseRecordServiceImpl();
    private static final OutwardService outwardService = new OutwardServiceImpl();
    private static final BackdownService backdownService = new BackdownServiceImpl();
    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        if (!login()) {
            return;
        }
        mainLoop();
    }

    private static boolean login() {
        System.out.println("欢迎进入东软颐养中心管理系统");
        for (int attempt = 0; attempt < 3; attempt++) {
            String username = ConsoleUtil.readNonEmpty("账号: ");
            String password = ConsoleUtil.readNonEmpty("密码: ");
            Result<User> result = loginService.login(username, password);
            if (result.isSuccess()) {
                Session.login(result.getData());
                System.out.println("登录成功，欢迎您 " + result.getData().getNickname());
                return true;
            }
            System.out.println("  >> " + result.getMessage());
        }
        System.out.println("登录失败次数过多，退出系统");
        return false;
    }

    /** 主菜单：按角色分流 */
    private static void mainLoop() {
        if (Session.isAdmin()) {
            adminLoop();
        } else {
            butlerLoop();
        }
    }

    /** 管理员主菜单 */
    private static void adminLoop() {
        while (true) {
            ConsoleUtil.menu("主菜单（管理员：" + Session.getCurrentUser().getNickname() + "）",
                    "床位示意图", "入住登记", "床位调换", "退住审核", "外出审核",
                    "护理管理", "健康管家", "服务关注", "床位管理", "用户管理", "退出登录");
            switch (ConsoleUtil.choice()) {
                case 1 -> showBedSchematic();
                case 2 -> checkIn();
                case 3 -> swapBed();
                case 4 -> auditBackdown();
                case 5 -> auditOutward();
                case 6 -> nurseManagementMenu();
                case 7 -> healthButlerMenu();
                case 8 -> serviceMonitorMenu();
                case 9 -> bedManage();
                case 10 -> userManage();
                case 11 -> {
                    Session.logout();
                    System.out.println("已退出，感谢使用");
                    return;
                }
                case 0 -> System.out.println("  >> 请从上方选择功能");
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 健康管家主菜单 */
    private static void butlerLoop() {
        while (true) {
            ConsoleUtil.menu("主菜单（健康管家：" + Session.getCurrentUser().getNickname() + "）",
                    "床位示意图", "日常护理", "服务对象护理记录", "退住申请", "外出申请", "退出登录");
            switch (ConsoleUtil.choice()) {
                case 1 -> showBedSchematic();
                case 2 -> dailyNurseMenu();
                case 3 -> butlerRecordMenu();
                case 4 -> butlerBackdownMenu();
                case 5 -> butlerOutwardMenu();
                case 6 -> {
                    Session.logout();
                    System.out.println("已退出，感谢使用");
                    return;
                }
                case 0 -> System.out.println("  >> 请从上方选择功能");
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    // ==================== 以下为既有功能（保持不变） ====================

    /** 床位示意图 */
    private static void showBedSchematic() {
        System.out.printf("%-6s%-10s%-10s%-10s%n", "ID", "房间号", "床位号", "状态");
        for (Bed bed : bedService.listAllBeds()) {
            System.out.printf("%-6d%-10d%-10s%-10s%n",
                    bed.getId(), bed.getRoomNo(), bed.getBedNo(), bedStatusText(bed.getBedStatus()));
        }
        ConsoleUtil.pause();
    }

    /** 入住登记 */
    private static void checkIn() {
        Customer c = new Customer();
        c.setCustomerName(ConsoleUtil.readNonEmpty("客户姓名: "));
        c.setCustomerSex(ConsoleUtil.readInt("性别(0-男 1-女): "));
        c.setIdcard(ConsoleUtil.readNonEmpty("身份证号: "));
        c.setBirthday(ConsoleUtil.readDate("出生日期(yyyy-MM-dd): "));
        c.setContactTel(ConsoleUtil.readNonEmpty("联系电话: "));
        c.setFamilyMember(ConsoleUtil.readNonEmpty("家属: "));
        c.setHeight(ConsoleUtil.readNonEmpty("身高: "));
        c.setWeight(ConsoleUtil.readNonEmpty("体重: "));
        c.setBloodType(ConsoleUtil.readNonEmpty("血型: "));
        c.setPsychosomaticState(ConsoleUtil.readNonEmpty("身体状况: "));
        c.setAttention(ConsoleUtil.readNonEmpty("注意事项: "));
        c.setFilepath("");
        c.setCheckinDate(ConsoleUtil.readDate("入住时间(yyyy-MM-dd): "));
        c.setExpirationDate(ConsoleUtil.readDate("合同到期时间(yyyy-MM-dd): "));
        int roomNo = ConsoleUtil.readInt("房间号: ");
        c.setLevelId(ConsoleUtil.readInt("护理等级ID: "));
        c.setUserId(-1);

        System.out.println("该房间空闲床位：");
        var freeBeds = bedService.listFreeBeds(roomNo);
        if (freeBeds.isEmpty()) {
            System.out.println("  >> 无空闲床位，登记终止");
            ConsoleUtil.pause();
            return;
        }
        for (Bed bed : freeBeds) {
            System.out.printf("  床位ID=%d 床位号=%s%n", bed.getId(), bed.getBedNo());
        }
        c.setBedId(ConsoleUtil.readInt("选择床位ID: "));

        Result<Integer> result = customerService.checkIn(c);
        System.out.println(result.isSuccess()
                ? "入住登记成功，客户ID=" + result.getData()
                : "  >> 登记失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    /** 床位调换 */
    private static void swapBed() {
        int customerId = ConsoleUtil.readInt("客户ID: ");
        Customer customer = customerService.findById(customerId);
        if (customer == null) {
            System.out.println("  >> 客户不存在");
            ConsoleUtil.pause();
            return;
        }
        int roomNo = ConsoleUtil.readInt("目标房间号: ");
        System.out.println("该房间空闲床位：");
        for (Bed bed : bedService.listFreeBeds(roomNo)) {
            System.out.printf("  床位ID=%d 床位号=%s%n", bed.getId(), bed.getBedNo());
        }
        int newBedId = ConsoleUtil.readInt("选择新床位ID: ");
        Result<Void> result = bedService.swapBed(customerId, newBedId);
        System.out.println(result.isSuccess() ? "床位调换成功" : "  >> 调换失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    /** 健康管家 - 退住申请菜单 */
    private static void butlerBackdownMenu() {
        while (true) {
            ConsoleUtil.menu("退住申请", "提交退住申请", "我的退住申请列表");
            switch (ConsoleUtil.choice()) {
                case 1 -> submitBackdown();
                case 2 -> listMyBackdown();
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 健康管家 - 外出申请菜单 */
    private static void butlerOutwardMenu() {
        while (true) {
            ConsoleUtil.menu("外出申请", "提交外出申请", "我的外出申请列表", "登记回院时间");
            switch (ConsoleUtil.choice()) {
                case 1 -> submitOutward();
                case 2 -> listMyOutward();
                case 3 -> registerReturn();
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 管理员 - 退住审核 */
    private static void auditBackdown() {
        String name = ConsoleUtil.readOptional("客户姓名(模糊，回车查全部): ");
        List<Backdown> list = backdownService.listByCustomerName(name);
        printBackdownList(list);
        if (list.isEmpty()) {
            ConsoleUtil.pause();
            return;
        }
        int id = ConsoleUtil.readInt("选择登记ID: ");
        int choice = ConsoleUtil.readInt("审核结果(1-同意 2-拒绝): ");
        Result<Void> result = backdownService.audit(id, choice == 1, Session.getCurrentUser().getNickname());
        System.out.println(result.isSuccess() ? "审核完成" : "  >> 审核失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    /** 管理员 - 外出审核 */
    private static void auditOutward() {
        String name = ConsoleUtil.readOptional("客户姓名(模糊，回车查全部): ");
        List<Outward> list = outwardService.listByCustomerName(name);
        printOutwardList(list);
        if (list.isEmpty()) {
            ConsoleUtil.pause();
            return;
        }
        int id = ConsoleUtil.readInt("选择登记ID: ");
        int choice = ConsoleUtil.readInt("审核结果(1-同意 2-拒绝): ");
        Result<Void> result = outwardService.audit(id, choice == 1, Session.getCurrentUser().getNickname());
        System.out.println(result.isSuccess() ? "审核完成" : "  >> 审核失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    /** 健康管家 - 提交退住申请 */
    private static void submitBackdown() {
        List<Customer> customers = customerService.listByUserId(Session.getCurrentUser().getId());
        if (customers.isEmpty()) {
            System.out.println("  >> 您暂无服务的客户");
            ConsoleUtil.pause();
            return;
        }
        printCustomers(customers);
        Backdown b = new Backdown();
        b.setCustomerId(ConsoleUtil.readInt("选择客户ID: "));
        System.out.println("退住类型：0-正常退住  1-死亡退住  2-保留床位");
        b.setRetreattype(ConsoleUtil.readInt("退住类型: "));
        b.setRetreatreason(ConsoleUtil.readNonEmpty("退住原因: "));
        b.setRetreattime(ConsoleUtil.readDate("退住时间(yyyy-MM-dd): "));
        b.setRemarks("");
        Result<Integer> result = backdownService.submit(b);
        System.out.println(result.isSuccess()
                ? "退住申请提交成功，登记ID=" + result.getData()
                : "  >> 提交失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    /** 健康管家 - 我的退住申请列表 */
    private static void listMyBackdown() {
        String name = ConsoleUtil.readOptional("客户姓名(模糊，回车查全部): ");
        List<Backdown> list = backdownService.listByCustomerNameAndUserId(name, Session.getCurrentUser().getId());
        printBackdownList(list);
        ConsoleUtil.pause();
    }

    /** 健康管家 - 提交外出申请 */
    private static void submitOutward() {
        List<Customer> customers = customerService.listByUserId(Session.getCurrentUser().getId());
        if (customers.isEmpty()) {
            System.out.println("  >> 您暂无服务的客户");
            ConsoleUtil.pause();
            return;
        }
        printCustomers(customers);
        Outward o = new Outward();
        o.setCustomerId(ConsoleUtil.readInt("选择客户ID: "));
        o.setOutgoingreason(ConsoleUtil.readNonEmpty("外出事由: "));
        o.setOutgoingtime(ConsoleUtil.readDate("外出时间(yyyy-MM-dd): "));
        o.setExpectedreturntime(ConsoleUtil.readDate("预计回院时间(yyyy-MM-dd): "));
        o.setEscorted(ConsoleUtil.readOptional("陪同人: "));
        o.setRelation(ConsoleUtil.readOptional("与陪同人关系: "));
        o.setEscortedtel(ConsoleUtil.readOptional("陪同人电话: "));
        o.setRemarks("");
        Result<Integer> result = outwardService.submit(o);
        System.out.println(result.isSuccess()
                ? "外出申请提交成功，登记ID=" + result.getData()
                : "  >> 提交失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    /** 健康管家 - 我的外出申请列表 */
    private static void listMyOutward() {
        String name = ConsoleUtil.readOptional("客户姓名(模糊，回车查全部): ");
        List<Outward> list = outwardService.listByCustomerNameAndUserId(name, Session.getCurrentUser().getId());
        printOutwardList(list);
        ConsoleUtil.pause();
    }

    /** 健康管家 - 登记实际回院时间 */
    private static void registerReturn() {
        String name = ConsoleUtil.readOptional("客户姓名(模糊，回车查全部): ");
        List<Outward> list = outwardService.listByCustomerNameAndUserId(name, Session.getCurrentUser().getId());
        printOutwardList(list);
        if (list.isEmpty()) {
            ConsoleUtil.pause();
            return;
        }
        int id = ConsoleUtil.readInt("选择登记ID: ");
        LocalDate actual = ConsoleUtil.readDate("实际回院时间(yyyy-MM-dd): ");
        Result<Void> result = outwardService.registerReturn(id, actual);
        System.out.println(result.isSuccess() ? "回院时间登记成功" : "  >> 登记失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    /** 打印退住登记列表 */
    private static void printBackdownList(List<Backdown> list) {
        if (list.isEmpty()) {
            System.out.println("  >> 无退住登记");
            return;
        }
        System.out.printf("%-6s%-10s%-10s%-12s%-12s%-8s%n",
                "ID", "客户", "退住类型", "退住时间", "退住原因", "审批状态");
        for (Backdown b : list) {
            System.out.printf("%-6d%-10s%-10s%-12s%-12s%-8s%n",
                    b.getId(), b.getCustomerName(), retreatTypeText(b.getRetreattype()),
                    b.getRetreattime(), b.getRetreatreason(), auditStatusText(b.getAuditstatus()));
        }
    }

    /** 打印外出登记列表 */
    private static void printOutwardList(List<Outward> list) {
        if (list.isEmpty()) {
            System.out.println("  >> 无外出登记");
            return;
        }
        System.out.printf("%-6s%-10s%-10s%-12s%-12s%-8s%n",
                "ID", "客户", "外出事由", "外出时间", "预计回院", "审批状态");
        for (Outward o : list) {
            System.out.printf("%-6d%-10s%-10s%-12s%-12s%-8s%n",
                    o.getId(), o.getCustomerName(), o.getOutgoingreason(),
                    o.getOutgoingtime(), o.getExpectedreturntime(), auditStatusText(o.getAuditstatus()));
        }
    }

    // ==================== 护理管理（管理员） ====================

    /** 护理管理主菜单 */
    private static void nurseManagementMenu() {
        while (true) {
            ConsoleUtil.menu("护理管理", "护理级别", "护理项目", "客户护理设置", "护理记录");
            switch (ConsoleUtil.choice()) {
                case 1 -> nurseLevelMenu();
                case 2 -> nurseContentMenu();
                case 3 -> customerNurseSetupMenu();
                case 4 -> adminNurseRecordMenu();
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 护理级别：查询 / 新增 / 改状态 / 级别项目配置 */
    private static void nurseLevelMenu() {
        while (true) {
            ConsoleUtil.menu("护理级别", "查询级别列表", "新增级别", "修改级别状态", "级别项目配置");
            switch (ConsoleUtil.choice()) {
                case 1 -> {
                    Integer status = statusFilter("查询级别");
                    printLevels(nurseLevelService.listLevels(status));
                    ConsoleUtil.pause();
                }
                case 2 -> {
                    NurseLevel level = new NurseLevel();
                    level.setLevelName(ConsoleUtil.readNonEmpty("级别名称: "));
                    level.setLevelStatus(statusInput("级别状态"));
                    Result<Integer> r = nurseLevelService.addLevel(level);
                    System.out.println(r.isSuccess() ? "新增成功，级别ID=" + r.getData()
                            : "  >> 新增失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 3 -> {
                    printLevels(nurseLevelService.listLevels(null));
                    int id = ConsoleUtil.readInt("选择级别ID: ");
                    int status = statusInput("新状态");
                    Result<Void> r = nurseLevelService.updateLevelStatus(id, status);
                    System.out.println(r.isSuccess() ? "状态修改成功" : "  >> 修改失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 4 -> nurseLevelItemConfig();
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 级别项目配置：查看 / 添加 / 移除 */
    private static void nurseLevelItemConfig() {
        printLevels(nurseLevelService.listLevels(null));
        int levelId = ConsoleUtil.readInt("选择要配置的级别ID: ");
        while (true) {
            ConsoleUtil.menu("级别[" + levelId + "]项目配置", "查看已配置项目", "添加项目", "移除项目");
            switch (ConsoleUtil.choice()) {
                case 1 -> {
                    printContents(nurseLevelService.listItemsInLevel(levelId));
                    ConsoleUtil.pause();
                }
                case 2 -> {
                    List<NurseContent> candidates = nurseLevelService.listItemsToAdd(levelId);
                    if (candidates.isEmpty()) {
                        System.out.println("  >> 没有可添加的启用项目");
                    } else {
                        printContents(candidates);
                    }
                    int itemId = ConsoleUtil.readInt("选择要添加的项目ID(无则填0): ");
                    if (itemId == 0) {
                        ConsoleUtil.pause();
                        break;
                    }
                    Result<Void> r = nurseLevelService.addItemToLevel(levelId, itemId);
                    System.out.println(r.isSuccess() ? "添加成功" : "  >> 添加失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 3 -> {
                    printContents(nurseLevelService.listItemsInLevel(levelId));
                    int itemId = ConsoleUtil.readInt("选择要移除的项目ID: ");
                    Result<Void> r = nurseLevelService.removeItemFromLevel(levelId, itemId);
                    System.out.println(r.isSuccess() ? "移除成功" : "  >> 移除失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 护理项目：查询 / 新增 / 修改（停用级联） / 逻辑删除 */
    private static void nurseContentMenu() {
        while (true) {
            ConsoleUtil.menu("护理项目", "查询项目列表", "新增项目", "修改项目", "逻辑删除项目");
            switch (ConsoleUtil.choice()) {
                case 1 -> {
                    Integer status = statusFilter("查询项目");
                    String name = ConsoleUtil.readNonEmpty("名称关键字(输入 all 表示不限): ");
                    if ("all".equalsIgnoreCase(name.trim())) {
                        name = null;
                    }
                    printContents(nurseContentService.listItems(status, name));
                    ConsoleUtil.pause();
                }
                case 2 -> {
                    NurseContent c = readNurseContent();
                    Result<Integer> r = nurseContentService.addItem(c);
                    System.out.println(r.isSuccess() ? "新增成功，项目ID=" + r.getData()
                            : "  >> 新增失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 3 -> {
                    printContents(nurseContentService.listItems(null, null));
                    int id = ConsoleUtil.readInt("选择要修改的项目ID: ");
                    NurseContent c = readNurseContent();
                    c.setId(id);
                    Result<Void> r = nurseContentService.updateItem(c);
                    System.out.println(r.isSuccess() ? "修改成功（若改为停用，各级别下该项目已剔除）"
                            : "  >> 修改失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 4 -> {
                    printContents(nurseContentService.listItems(null, null));
                    int id = ConsoleUtil.readInt("选择要删除的项目ID: ");
                    Result<Void> r = nurseContentService.logicDeleteItem(id);
                    System.out.println(r.isSuccess() ? "删除成功（客户已购项目不受影响）"
                            : "  >> 删除失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 客户护理设置：查询客户 / 设置级别 / 移除级别 */
    private static void customerNurseSetupMenu() {
        while (true) {
            ConsoleUtil.menu("客户护理设置", "查询客户", "设置护理级别", "移除护理级别");
            switch (ConsoleUtil.choice()) {
                case 1 -> {
                    printCustomers(customerNurseService.listCustomers(searchName()));
                    ConsoleUtil.pause();
                }
                case 2 -> {
                    int customerId = chooseCustomer(customerNurseService.listCustomers(searchName()));
                    if (customerId <= 0) {
                        ConsoleUtil.pause();
                        break;
                    }
                    printLevels(nurseLevelService.listLevels(NurseStatusConstant.ENABLED));
                    int levelId = ConsoleUtil.readInt("选择护理级别ID: ");
                    System.out.println("该级别将随设置一并购买的护理项目：");
                    printContents(nurseLevelService.listItemsInLevel(levelId));
                    Result<Void> r = customerNurseService.setLevel(customerId, levelId);
                    System.out.println(r.isSuccess() ? "设置成功，已批量写入护理项目（数量1/购买日今天/到期+3月）"
                            : "  >> 设置失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 3 -> {
                    int customerId = chooseCustomer(customerNurseService.listCustomers(searchName()));
                    if (customerId <= 0) {
                        ConsoleUtil.pause();
                        break;
                    }
                    System.out.println("  >> 谨慎操作：移除级别将清掉该级别下全部护理项目！");
                    String confirm = ConsoleUtil.readNonEmpty("确认移除？(y/n): ");
                    if (!"y".equalsIgnoreCase(confirm.trim())) {
                        System.out.println("已取消");
                        ConsoleUtil.pause();
                        break;
                    }
                    Result<Void> r = customerNurseService.removeLevel(customerId);
                    System.out.println(r.isSuccess() ? "移除成功，客户护理级别已置空"
                            : "  >> 移除失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 管理员护理记录：选客户查记录 / 删除记录 */
    private static void adminNurseRecordMenu() {
        while (true) {
            ConsoleUtil.menu("护理记录", "查看客户护理记录", "移除护理记录");
            switch (ConsoleUtil.choice()) {
                case 1 -> {
                    int customerId = chooseCustomer(customerNurseService.listCustomers(searchName()));
                    if (customerId > 0) {
                        printRecords(nurseRecordService.listByCustomerId(customerId));
                    }
                    ConsoleUtil.pause();
                }
                case 2 -> {
                    int customerId = chooseCustomer(customerNurseService.listCustomers(searchName()));
                    if (customerId <= 0) {
                        ConsoleUtil.pause();
                        break;
                    }
                    List<NurseRecord> records = nurseRecordService.listByCustomerId(customerId);
                    printRecords(records);
                    if (records.isEmpty()) {
                        ConsoleUtil.pause();
                        break;
                    }
                    int recordId = ConsoleUtil.readInt("选择要移除的记录ID: ");
                    Result<Void> r = nurseRecordService.logicDelete(recordId);
                    System.out.println(r.isSuccess() ? "移除成功" : "  >> 移除失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    // ==================== 健康管家（管理员设置服务对象） ====================

    /** 健康管家主菜单：查询管家 / 设置服务对象 */
    private static void healthButlerMenu() {
        while (true) {
            ConsoleUtil.menu("健康管家", "查询健康管家", "设置服务对象");
            switch (ConsoleUtil.choice()) {
                case 1 -> {
                    String name = butlerSearchName();
                    List<User> butlers = customerNurseService.listButlers(name);
                    printButlers(butlers);
                    ConsoleUtil.pause();
                }
                case 2 -> {
                    String name = butlerSearchName();
                    List<User> butlers = customerNurseService.listButlers(name);
                    if (butlers.isEmpty()) {
                        System.out.println("  >> 没有匹配的健康管家");
                        ConsoleUtil.pause();
                        break;
                    }
                    printButlers(butlers);
                    int butlerId = ConsoleUtil.readInt("选择管家ID: ");
                    butlerCustomerManageMenu(butlerId);
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 管家客户管理：查看服务客户 / 添加客户 / 移除客户 */
    private static void butlerCustomerManageMenu(int butlerId) {
        while (true) {
            ConsoleUtil.menu("管家[ID=" + butlerId + "]服务对象管理",
                    "查看服务客户列表", "添加服务客户", "移除服务客户");
            switch (ConsoleUtil.choice()) {
                case 1 -> {
                    List<Customer> customers = customerService.listByUserId(butlerId);
                    if (customers.isEmpty()) {
                        System.out.println("  >> 该管家暂无服务客户");
                    } else {
                        printCustomers(customers);
                    }
                    ConsoleUtil.pause();
                }
                case 2 -> {
                    String name = searchName();
                    List<Customer> noButlerCustomers = customerService.listWithoutButler(name);
                    if (noButlerCustomers.isEmpty()) {
                        System.out.println("  >> 没有无管家的客户");
                        ConsoleUtil.pause();
                        break;
                    }
                    System.out.println("无管家客户列表：");
                    printCustomers(noButlerCustomers);
                    int customerId = ConsoleUtil.readInt("选择要添加的客户ID(0取消): ");
                    if (customerId == 0) {
                        ConsoleUtil.pause();
                        break;
                    }
                    Result<Void> r = customerService.assignButler(customerId, butlerId);
                    System.out.println(r.isSuccess() ? "添加成功" : "  >> 添加失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 3 -> {
                    List<Customer> customers = customerService.listByUserId(butlerId);
                    if (customers.isEmpty()) {
                        System.out.println("  >> 该管家暂无服务客户");
                        ConsoleUtil.pause();
                        break;
                    }
                    printCustomers(customers);
                    int customerId = ConsoleUtil.readInt("选择要移除的客户ID(0取消): ");
                    if (customerId == 0) {
                        ConsoleUtil.pause();
                        break;
                    }
                    System.out.println("  >> 移除操作不会影响护理记录信息");
                    String confirm = ConsoleUtil.readNonEmpty("确认移除？(y/n): ");
                    if (!"y".equalsIgnoreCase(confirm.trim())) {
                        System.out.println("已取消");
                        ConsoleUtil.pause();
                        break;
                    }
                    Result<Void> r = customerService.removeButler(customerId);
                    System.out.println(r.isSuccess() ? "移除成功" : "  >> 移除失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    // ==================== 服务关注（管理员） ====================

    /** 服务关注主菜单 */
    private static void serviceMonitorMenu() {
        while (true) {
            ConsoleUtil.menu("服务关注", "查询客户信息列表");
            switch (ConsoleUtil.choice()) {
                case 1 -> {
                    String name = searchName();
                    List<Customer> customers = customerNurseService.listCustomers(name);
                    if (customers.isEmpty()) {
                        System.out.println("  >> 没有匹配的客户");
                        ConsoleUtil.pause();
                        break;
                    }
                    printCustomers(customers);
                    int customerId = ConsoleUtil.readInt("选择客户ID(0返回): ");
                    if (customerId == 0) {
                        ConsoleUtil.pause();
                        break;
                    }
                    serviceMonitorCustomerMenu(customerId);
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 服务关注 - 客户护理项目管理：查看 / 续费 / 移除 / 购买 */
    private static void serviceMonitorCustomerMenu(int customerId) {
        while (true) {
            ConsoleUtil.menu("服务关注", "查看护理服务项目列表", "续费", "移除", "购买护理项目");
            switch (ConsoleUtil.choice()) {
                case 1 -> {
                    List<CustomerNurseItem> items = customerNurseService.listCustomerItems(customerId);
                    if (items.isEmpty()) {
                        System.out.println("  >> 该客户暂无护理服务项目");
                    } else {
                        printCustomerItems(items);
                    }
                    ConsoleUtil.pause();
                }
                case 2 -> {
                    List<CustomerNurseItem> items = customerNurseService.listCustomerItems(customerId);
                    if (items.isEmpty()) {
                        System.out.println("  >> 该客户暂无护理服务项目");
                        ConsoleUtil.pause();
                        break;
                    }
                    printCustomerItems(items);
                    int cniId = ConsoleUtil.readInt("选择要续费的项目(客户护理项ID，0取消): ");
                    if (cniId == 0) {
                        ConsoleUtil.pause();
                        break;
                    }
                    int addNumber = ConsoleUtil.readInt("新增数量: ");
                    LocalDate newMaturity = ConsoleUtil.readDate("新的服务到期时间(yyyy-MM-dd): ");
                    Result<Void> r = customerNurseService.renewItem(cniId, addNumber, newMaturity);
                    System.out.println(r.isSuccess() ? "续费成功" : "  >> 续费失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 3 -> {
                    List<CustomerNurseItem> items = customerNurseService.listCustomerItems(customerId);
                    if (items.isEmpty()) {
                        System.out.println("  >> 该客户暂无护理服务项目");
                        ConsoleUtil.pause();
                        break;
                    }
                    printCustomerItems(items);
                    int cniId = ConsoleUtil.readInt("选择要移除的项目(客户护理项ID，0取消): ");
                    if (cniId == 0) {
                        ConsoleUtil.pause();
                        break;
                    }
                    System.out.println("  >> 谨慎操作：移除后客户将不再享有对应的服务！");
                    String confirm = ConsoleUtil.readNonEmpty("确认移除？(y/n): ");
                    if (!"y".equalsIgnoreCase(confirm.trim())) {
                        System.out.println("已取消");
                        ConsoleUtil.pause();
                        break;
                    }
                    Result<Void> r = customerNurseService.removeItem(cniId);
                    System.out.println(r.isSuccess() ? "移除成功" : "  >> 移除失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 4 -> purchaseNurseItemMenu(customerId);
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 购买护理项目 */
    private static void purchaseNurseItemMenu(int customerId) {
        String name = ConsoleUtil.readOptional("护理项目名称(模糊，回车查全部): ");
        List<NurseContent> available = customerNurseService.listAvailableItems(customerId,
                name.isEmpty() ? null : name);
        if (available.isEmpty()) {
            System.out.println("  >> 没有可选的护理项目（可能已全部购买或无启用项目）");
            ConsoleUtil.pause();
            return;
        }
        printContents(available);
        int itemId = ConsoleUtil.readInt("选择要购买的护理项目ID(0取消): ");
        if (itemId == 0) {
            ConsoleUtil.pause();
            return;
        }
        int number = ConsoleUtil.readInt("数量(默认1): ");
        if (number <= 0) number = 1;
        LocalDate maturity = ConsoleUtil.readDate("服务到期时间(yyyy-MM-dd，默认今天，直接回车): ");
        Result<Void> r = customerNurseService.purchaseItem(customerId, itemId, number, maturity);
        System.out.println(r.isSuccess() ? "购买成功" : "  >> 购买失败: " + r.getMessage());
        ConsoleUtil.pause();
    }

    // ==================== 健康管家（管家端） ====================

    /** 日常护理：选自己服务客户 → 选项目 → 执行护理（生成记录+扣数量） */
    private static void dailyNurseMenu() {
        int butlerId = Session.getCurrentUser().getId();
        while (true) {
            ConsoleUtil.menu("日常护理", "选择服务客户并护理");
            switch (ConsoleUtil.choice()) {
                case 1 -> {
                    int customerId = chooseCustomer(customerNurseService.listMyCustomers(butlerId, searchName()));
                    if (customerId <= 0) {
                        ConsoleUtil.pause();
                        break;
                    }
                    List<CustomerNurseItem> items = customerNurseService.listCustomerItems(customerId);
                    printCustomerItems(items);
                    if (items.isEmpty()) {
                        System.out.println("  >> 该客户暂无护理项目");
                        ConsoleUtil.pause();
                        break;
                    }
                    int cniId = ConsoleUtil.readInt("选择要护理的项目(客户护理项ID): ");
                    int count = ConsoleUtil.readInt("本次护理数量: ");
                    String content = ConsoleUtil.readNonEmpty("护理内容: ");
                    LocalDateTime time = ConsoleUtil.readDateTime("护理时间(yyyy-MM-dd HH:mm:ss，回车=当前): ");
                    Result<Integer> r = customerNurseService.doDailyNurse(cniId, butlerId, time, content, count);
                    System.out.println(r.isSuccess() ? "护理完成，记录ID=" + r.getData()
                            : "  >> 护理失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    /** 服务对象护理记录：查自己客户记录 / 移除 */
    private static void butlerRecordMenu() {
        int butlerId = Session.getCurrentUser().getId();
        while (true) {
            ConsoleUtil.menu("服务对象护理记录", "查看护理记录", "移除护理记录");
            switch (ConsoleUtil.choice()) {
                case 1 -> {
                    int customerId = chooseCustomer(customerNurseService.listMyCustomers(butlerId, searchName()));
                    if (customerId > 0) {
                        printRecords(nurseRecordService.listByCustomerId(customerId));
                    }
                    ConsoleUtil.pause();
                }
                case 2 -> {
                    int customerId = chooseCustomer(customerNurseService.listMyCustomers(butlerId, searchName()));
                    if (customerId <= 0) {
                        ConsoleUtil.pause();
                        break;
                    }
                    List<NurseRecord> records = nurseRecordService.listByCustomerId(customerId);
                    printRecords(records);
                    if (records.isEmpty()) {
                        ConsoleUtil.pause();
                        break;
                    }
                    int recordId = ConsoleUtil.readInt("选择要移除的记录ID: ");
                    Result<Void> r = nurseRecordService.logicDelete(recordId);
                    System.out.println(r.isSuccess() ? "移除成功" : "  >> 移除失败: " + r.getMessage());
                    ConsoleUtil.pause();
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    // ==================== 交互辅助 ====================

    /** 读取护理项目各字段（不含ID） */
    private static NurseContent readNurseContent() {
        NurseContent c = new NurseContent();
        c.setSerialNumber(ConsoleUtil.readNonEmpty("编号(serial_number): "));
        c.setNursingName(ConsoleUtil.readNonEmpty("名称: "));
        c.setServicePrice(ConsoleUtil.readNonEmpty("价格: "));
        c.setMessage(ConsoleUtil.readNonEmpty("描述: "));
        c.setStatus(statusInput("项目状态"));
        c.setExecutionCycle(ConsoleUtil.readNonEmpty("执行周期: "));
        c.setExecutionTimes(ConsoleUtil.readNonEmpty("执行次数: "));
        return c;
    }

    /** 状态过滤：返回 1/2/null(全部) */
    private static Integer statusFilter(String action) {
        String s = ConsoleUtil.readNonEmpty(action + " - 状态过滤(1-启用 2-停用 3-全部): ");
        return switch (s.trim()) {
            case "1" -> NurseStatusConstant.ENABLED;
            case "2" -> NurseStatusConstant.DISABLED;
            default -> null;
        };
    }

    /** 状态输入：1-启用 2-停用 */
    private static int statusInput(String label) {
        while (true) {
            int v = ConsoleUtil.readInt(label + "(1-启用 2-停用): ");
            if (v == 1 || v == 2) {
                return v;
            }
            System.out.println("  >> 只能输入 1 或 2");
        }
    }

    /** 姓名关键字输入（all 表示不限） */
    private static String searchName() {
        String name = ConsoleUtil.readNonEmpty("客户姓名关键字(输入 all 表示全部): ");
        return "all".equalsIgnoreCase(name.trim()) ? null : name.trim();
    }

    /** 管家姓名关键字输入（模糊，回车查全部） */
    private static String butlerSearchName() {
        String name = ConsoleUtil.readOptional("管家姓名关键字(模糊，回车查全部): ");
        return name.isEmpty() ? null : name.trim();
    }

    /** 打印客户列表并返回选择ID（列表为空或选0返回-1） */
    private static int chooseCustomer(List<Customer> customers) {
        if (customers == null || customers.isEmpty()) {
            System.out.println("  >> 没有匹配的客户");
            return -1;
        }
        printCustomers(customers);
        return ConsoleUtil.readInt("选择客户ID(0取消): ");
    }

    /** 打印客户列表 */
    private static void printCustomers(List<Customer> customers) {
        System.out.printf("%-6s%-14s%-8s%-12s%-12s%-14s%n", "ID", "姓名", "性别", "房间号", "护理级别", "联系电话");
        for (Customer c : customers) {
            System.out.printf("%-6d%-14s%-8s%-12s%-12s%-14s%n",
                    c.getId(), c.getCustomerName(), sexText(c.getCustomerSex()), c.getRoomNo(),
                    c.getLevelId() == null ? "无" : c.getLevelId(), c.getContactTel());
        }
    }

    private static void printLevels(List<NurseLevel> levels) {
        System.out.printf("%-6s%-20s%-12s%n", "ID", "级别名称", "状态");
        for (NurseLevel l : levels) {
            System.out.printf("%-6d%-20s%-12s%n", l.getId(), l.getLevelName(), nurseStatusText(l.getLevelStatus()));
        }
    }

    private static void printContents(List<NurseContent> contents) {
        System.out.printf("%-6s%-14s%-16s%-10s%-10s%-12s%-10s%s%n",
                "ID", "编号", "名称", "价格", "状态", "周期", "次数", "描述");
        for (NurseContent c : contents) {
            System.out.printf("%-6d%-14s%-16s%-10s%-10s%-12s%-10s%s%n",
                    c.getId(), c.getSerialNumber(), c.getNursingName(), c.getServicePrice(),
                    nurseStatusText(c.getStatus()), c.getExecutionCycle(), c.getExecutionTimes(), c.getMessage());
        }
    }

    /** 打印管家列表 */
    private static void printButlers(List<User> butlers) {
        if (butlers.isEmpty()) {
            System.out.println("  >> 没有匹配的健康管家");
            return;
        }
        System.out.printf("%-6s%-14s%-10s%-16s%s%n", "ID", "姓名", "性别", "联系电话", "邮箱");
        for (User u : butlers) {
            System.out.printf("%-6d%-14s%-10s%-16s%s%n",
                    u.getId(), u.getNickname(), sexText(u.getSex()), u.getPhoneNumber(), u.getEmail());
        }
    }

    private static void printCustomerItems(List<CustomerNurseItem> items) {
        System.out.printf("%-8s%-16s%-12s%-10s%-14s%-14s%s%n",
                "客户护理项ID", "项目名称", "单价", "剩余数量", "购买日期", "到期日期", "服务状态");
        for (CustomerNurseItem i : items) {
            String status = ServiceStatusConstant.determine(i.getMaturityTime(), i.getNurseNumber());
            System.out.printf("%-8d%-16s%-12s%-10s%-14s%-14s%s%n",
                    i.getId(), i.getItemName(), i.getServicePrice(), i.getNurseNumber(),
                    i.getBuyTime(), i.getMaturityTime(), status);
        }
    }

    private static void printRecords(List<NurseRecord> records) {
        System.out.printf("%-6s%-16s%-22s%-10s%s%n", "ID", "项目名称", "护理时间", "次数", "护理内容");
        for (NurseRecord r : records) {
            System.out.printf("%-6d%-16s%-22s%-10d%s%n",
                    r.getId(), r.getItemName(), r.getNursingTime(), r.getNursingCount(), r.getNursingContent());
        }
    }

    private static String nurseStatusText(Integer status) {
        if (NurseStatusConstant.ENABLED.equals(status)) {
            return "启用";
        }
        if (NurseStatusConstant.DISABLED.equals(status)) {
            return "停用";
        }
        return String.valueOf(status);
    }

    private static String sexText(Integer sex) {
        if (Integer.valueOf(0).equals(sex)) {
            return "男";
        }
        if (Integer.valueOf(1).equals(sex)) {
            return "女";
        }
        return "未知";
    }

    private static String bedStatusText(Integer status) {
        if (BedStatusConstant.FREE.equals(status)) {
            return "空闲";
        }
        if (BedStatusConstant.OCCUPIED.equals(status)) {
            return "有人";
        }
        if (BedStatusConstant.OUTWARD.equals(status)) {
            return "外出";
        }
        return String.valueOf(status);
    }

    private static String retreatTypeText(Integer type) {
        if (RetreatTypeConstant.NORMAL.equals(type)) {
            return "正常退住";
        }
        if (RetreatTypeConstant.DEATH.equals(type)) {
            return "死亡退住";
        }
        if (RetreatTypeConstant.KEEP_BED.equals(type)) {
            return "保留床位";
        }
        return String.valueOf(type);
    }

    private static String auditStatusText(Integer status) {
        if (AuditStatusConstant.SUBMITTED.equals(status)) {
            return "已提交";
        }
        if (AuditStatusConstant.AGREE.equals(status)) {
            return "同意";
        }
        if (AuditStatusConstant.REJECT.equals(status)) {
            return "拒绝";
        }
        return String.valueOf(status);
    }

    // ==================== 床位管理（管理员） ====================

    /** 床位管理：查询/新增/修改/删除 */
    private static void bedManage() {
        while (true) {
            ConsoleUtil.menu("床位管理", "查询床位", "新增床位", "修改床位", "删除床位");
            switch (ConsoleUtil.choice()) {
                case 1 -> queryBeds();
                case 2 -> addBed();
                case 3 -> updateBed();
                case 4 -> deleteBed();
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    private static void queryBeds() {
        Integer roomNo = ConsoleUtil.readOptionalInt("房间号(直接回车忽略): ");
        String bedNo = ConsoleUtil.readOptional("床位号(直接回车忽略): ");
        Integer status = ConsoleUtil.readOptionalInt("状态(1-空闲 2-有人 3-外出，回车忽略): ");
        printBedTable(bedService.listByCondition(roomNo, bedNo, status));
        ConsoleUtil.pause();
    }

    private static void addBed() {
        Bed bed = new Bed();
        bed.setRoomNo(ConsoleUtil.readInt("房间号: "));
        bed.setBedNo(ConsoleUtil.readNonEmpty("床位号: "));
        bed.setRemarks(ConsoleUtil.readOptional("备注(可空): "));
        Result<Integer> result = bedService.addBed(bed);
        System.out.println(result.isSuccess()
                ? "新增床位成功，床位ID=" + result.getData()
                : "  >> 新增失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    private static void updateBed() {
        int id = ConsoleUtil.readInt("床位ID: ");
        Bed bed = bedService.findById(id);
        if (bed == null) {
            System.out.println("  >> 床位不存在");
            ConsoleUtil.pause();
            return;
        }
        printBedTable(List.of(bed));
        bed.setRoomNo(ConsoleUtil.readInt("新房间号: "));
        bed.setBedNo(ConsoleUtil.readNonEmpty("新床位号: "));
        bed.setBedStatus(ConsoleUtil.readInt("状态(1-空闲 2-有人 3-外出): "));
        bed.setRemarks(ConsoleUtil.readOptional("备注(可空): "));
        Result<Void> result = bedService.updateBed(bed);
        System.out.println(result.isSuccess() ? "修改床位成功" : "  >> 修改失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    private static void deleteBed() {
        int id = ConsoleUtil.readInt("床位ID: ");
        Result<Void> result = bedService.deleteBed(id);
        System.out.println(result.isSuccess() ? "删除床位成功" : "  >> 删除失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    private static void printBedTable(List<Bed> beds) {
        System.out.printf("%-6s%-10s%-10s%-10s%-16s%n", "ID", "房间号", "床位号", "状态", "备注");
        for (Bed bed : beds) {
            System.out.printf("%-6d%-10d%-10s%-10s%-16s%n",
                    bed.getId(), bed.getRoomNo(), bed.getBedNo(),
                    bedStatusText(bed.getBedStatus()), bed.getRemarks());
        }
    }

    // ==================== 用户管理（管理员） ====================

    /** 用户管理：仅管理员可进入 */
    private static void userManage() {
        if (!Session.isAdmin()) {
            System.out.println("  >> 无权限，用户管理仅管理员可操作");
            ConsoleUtil.pause();
            return;
        }
        while (true) {
            ConsoleUtil.menu("用户管理", "查询用户", "新增用户", "修改用户", "重置密码", "删除用户");
            switch (ConsoleUtil.choice()) {
                case 1 -> queryUsers();
                case 2 -> addUserMenu();
                case 3 -> updateUser();
                case 4 -> resetPassword();
                case 5 -> deleteUser();
                case 0 -> {
                    return;
                }
                default -> System.out.println("  >> 无效选择");
            }
        }
    }

    private static void queryUsers() {
        String nickname = ConsoleUtil.readOptional("姓名(模糊，回车忽略): ");
        Integer roleId = ConsoleUtil.readOptionalInt("角色(1-管理员 2-健康管家，回车忽略): ");
        printUserTable(userService.listByCondition(nickname, roleId));
        ConsoleUtil.pause();
    }

    private static void addUserMenu() {
        User user = new User();
        user.setNickname(ConsoleUtil.readNonEmpty("真实姓名: "));
        user.setUsername(ConsoleUtil.readNonEmpty("登录账号: "));
        int roleId = ConsoleUtil.readInt("角色(1-管理员 2-健康管家): ");
        user.setRoleId(roleId);
        if (RoleConstant.ADMIN.equals(roleId)) {
            user.setPassword(ConsoleUtil.readNonEmpty("登录密码: "));
        } else {
            System.out.println("  健康管家密码将默认为手机号后6位");
        }
        user.setSex(ConsoleUtil.readInt("性别(0-女 1-男): "));
        user.setEmail(ConsoleUtil.readNonEmpty("邮箱: "));
        String phone = ConsoleUtil.readNonEmpty("电话号码: ");
        user.setPhoneNumber(phone);
        user.setCreateBy(Session.getCurrentUser().getId());
        Result<Integer> result = userService.addUser(user);
        if (result.isSuccess()) {
            System.out.println("新增用户成功，用户ID=" + result.getData());
            if (!RoleConstant.ADMIN.equals(roleId)) {
                System.out.println("  初始密码为手机号后6位: " + tail6(phone));
            }
        } else {
            System.out.println("  >> 新增失败: " + result.getMessage());
        }
        ConsoleUtil.pause();
    }

    private static void updateUser() {
        int id = ConsoleUtil.readInt("用户ID: ");
        User user = userService.findById(id);
        if (user == null) {
            System.out.println("  >> 用户不存在");
            ConsoleUtil.pause();
            return;
        }
        printUserTable(List.of(user));
        user.setNickname(ConsoleUtil.readNonEmpty("真实姓名: "));
        user.setSex(ConsoleUtil.readInt("性别(0-女 1-男): "));
        user.setEmail(ConsoleUtil.readNonEmpty("邮箱: "));
        user.setPhoneNumber(ConsoleUtil.readNonEmpty("电话号码: "));
        user.setRoleId(ConsoleUtil.readInt("角色(1-管理员 2-健康管家): "));
        user.setUpdateBy(Session.getCurrentUser().getId());
        Result<Void> result = userService.updateUser(user);
        System.out.println(result.isSuccess() ? "修改用户成功" : "  >> 修改失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    private static void resetPassword() {
        int id = ConsoleUtil.readInt("用户ID: ");
        Result<String> result = userService.resetPassword(id, Session.getCurrentUser().getId());
        System.out.println(result.isSuccess()
                ? "密码已重置为手机号后6位: " + result.getData()
                : "  >> 重置失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    private static void deleteUser() {
        int id = ConsoleUtil.readInt("用户ID: ");
        Result<Void> result = userService.deleteUser(id, Session.getCurrentUser().getId());
        System.out.println(result.isSuccess() ? "删除用户成功" : "  >> 删除失败: " + result.getMessage());
        ConsoleUtil.pause();
    }

    private static void printUserTable(List<User> users) {
        System.out.printf("%-6s%-12s%-12s%-6s%-22s%-14s%-10s%n",
                "ID", "姓名", "账号", "性别", "邮箱", "电话", "角色");
        for (User u : users) {
            System.out.printf("%-6d%-12s%-12s%-6s%-22s%-14s%-10s%n",
                    u.getId(), u.getNickname(), u.getUsername(),
                    Integer.valueOf(1).equals(u.getSex()) ? "男" : "女",
                    u.getEmail(), u.getPhoneNumber(), roleText(u.getRoleId()));
        }
    }

    private static String roleText(Integer roleId) {
        if (RoleConstant.ADMIN.equals(roleId)) {
            return "管理员";
        }
        if (RoleConstant.HEALTH_BUTLER.equals(roleId)) {
            return "健康管家";
        }
        return String.valueOf(roleId);
    }

    private static String tail6(String phone) {
        if (phone == null || phone.length() < 6) {
            return "123456";
        }
        return phone.substring(phone.length() - 6);
    }
}