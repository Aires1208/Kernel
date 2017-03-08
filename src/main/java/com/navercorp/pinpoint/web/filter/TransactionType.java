package com.navercorp.pinpoint.web.filter;

/**
 * Created by 10170261 on 16-6-16.
 */
public class TransactionType {
    public static final String TRANSACTION_CM = "CM";
    public static final String TRANSACTION_FM = "FM";
    public static final String TRANSACTION_PM = "PM";
    public static final String TRANSACTION_RM = "RM";
    public static final String TRANSACTION_CHK = "CHECK";
    public static final String TRANSACTION_DDM = "DDM";
    public static final String TRANSACTION_NRM = "NRM";
    public static final String TRANSACTION_TOPO = "TOPO";
    public static final String TRANSACTION_FCT = "FCT";
    public static final String TRANSACTION_OTHER = "OTHER";

    //CM-CBC
    public static final int CM_CBC_BEGIN = 461000;
    public static final int CM_CBC_END = 461980;
    //CM_MOVESITE
    public static final int CM_MOVESITE_BEGIN = 180000;
    public static final int CM_MOVESITE_END = 180100;
    //CM_OTHER
    public static final int CM_OTHER_BEGIN = 126000;
    public static final int CM_OTHER_END = 126100;
    //CM_EMB
    public static final int CM_EMB_BEGIN = 10126000;
    public static final int CM_EMB_END = 10126999;


    //PM
    public static final int PM_F_BEGIN = 460400;
    public static final int PM_F_END = 460499;
    public static final int PM_EMB_BEGIN = 10100000;
    public static final int PM_EMB_END = 10109999;

    //FM
    public static final int FM_F_BEGIN = 460500;
    public static final int FM_F_END = 460529;
    public static final int FM_EMB_BEGIN = 10440000;
    public static final int FM_EMB_END = 10449999;

    //RM
    public static final int RM_F_BEGIN = 460000;
    public static final int RM_F_END = 460150;
    public static final int RM_EMS_BEGIN = 10001600;
    public static final int RM_EMS_END = 10009999;
    public static final int RM_EMB_BEGIN = 101250000;
    public static final int RM_EMB_END = 101259999;

    //NRM
    public static final int NRM_RM_BEGIN = 101250000;
    public static final int NRM_RM_END = 101259999;
    public static final int NRM_BEGIN = 10121000;
    public static final int NRM_END = 10121999;

    //DDM
    public static final int DDM_BEGIN_CODE = 460300;
    public static final int DDM_END_CODE = 460399;

    //TOPO
    public static final int TOPO_BEGIN_CODE = 467000;
    public static final int TOPO_END_CODE = 485099;

    //FCT
    public static final int FCT_BEGIN_CODE = 461981;
    public static final int FCT_END_CODE = 461999;

    //CHECK
    public static final int CHECK_BEGIN = 10156000;
    public static final int CHECK_END = 10156999;

    //CM_CBC
    public static final CodeRange CM_CBC_RANGE = new CodeRange(CM_CBC_BEGIN, CM_CBC_END);
    //CM_MOVESITE
    public static final CodeRange CM_MOVESITE_RANGE = new CodeRange(CM_MOVESITE_BEGIN, CM_MOVESITE_END);
    //CM_OTHER
    public static final CodeRange CM_OTHER_RANGE = new CodeRange(CM_OTHER_BEGIN, CM_OTHER_END);
    //CM_EMB
    public static final CodeRange CM_EMB_RANGE = new CodeRange(CM_EMB_BEGIN, CM_EMB_END);

    //FM
    public static final CodeRange FM_F_RANGE = new CodeRange(FM_F_BEGIN, FM_F_END);
    public static final CodeRange FM_EMB_RANGE = new CodeRange(FM_EMB_BEGIN, FM_EMB_END);

    //PM
    public static final CodeRange PM_F_RANGE = new CodeRange(PM_F_BEGIN, PM_F_END);
    public static final CodeRange PM_EMB_RANGE = new CodeRange(PM_EMB_BEGIN, PM_EMB_END);

    //RM
    public static final CodeRange RM_F_RANGE = new CodeRange(RM_F_BEGIN, RM_F_END);
    public static final CodeRange RM_EMS_RANGE = new CodeRange(RM_EMS_BEGIN, RM_EMS_END);
    public static final CodeRange RM_EMF_RANGE = new CodeRange(RM_EMB_BEGIN, RM_EMB_END);

    //NRM
    public static final CodeRange NRM_RM_RANGE = new CodeRange(NRM_RM_BEGIN, NRM_RM_END);
    public static final CodeRange NRM_EMB_RANGE = new CodeRange(NRM_BEGIN, NRM_END);

    //DDM
    public static final CodeRange DDM_RANGE = new CodeRange(DDM_BEGIN_CODE, DDM_END_CODE);

    //TOPO
    public static final CodeRange TOPO_RANGE = new CodeRange(TOPO_BEGIN_CODE, TOPO_END_CODE);

    //FCT
    public static final CodeRange FCT_RANGE = new CodeRange(FCT_BEGIN_CODE, FCT_END_CODE);

    //CHECK
    public static final CodeRange CHK_EMB_RANGE = new CodeRange(CHECK_BEGIN, CHECK_END);

}
