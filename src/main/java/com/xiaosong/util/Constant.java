package com.xiaosong.util;

public class Constant {
    /**
     * @param TEMPLATE_EYE_CROSS 最小眼间距
     */
    public final static int TEMPLATE_EYE_CROSS = 45;
    /**
     * @param TEMPLATE_ROLL_ANGL 最大旋转角度
     */
    public final static int TEMPLATE_ROLL_ANGL = 35;
    /**
     * @param TEMPLATE_CONFIDENCE 预期比对分数
     */
    public final static int TEMPLATE_CONFIDENCE = 65;
    /**
     * @param REQUEST_THREAD_WAIT_TIME 人脸建模线程等待时间长短
     */
    public final static int REQUEST_THREAD_WAIT_TIME = 100;
    /**
     * @param FACE_DETECT_ENGINE_SIZE 人脸建模线程大小
     */
    public final static int FACE_DETECT_ENGINE_SIZE = 4;
    /**
     * @param FACE_PIC_QUEUE_NAME redis数据库中，人脸图片数据队列名称
     */
    public final static String FACE_PIC_QUEUE_NAME = "facePicData";
    /**
     * @param FACE_MODULE_QUEUE_NAME redis数据库中，人脸模型数据队列名称
     */
    public final static String FACE_MODULE_QUEUE_NAME = "faceModuleData";
    /**
     * @param MONITOR_THREAD_SPACE_TIME 数据库监听线程休眠时间:5秒
     */
    public final static int MONITOR_THREAD_SPACE_TIME = 5000;

    public static class ErrorCode
    {
        /**
         * @param SERVER_NO_REACT 服务器没有响应
         */
        public final static int SERVER_NO_REACT = 404;

        /**
         * @param NO_FACE_DETECT 未检测到人像
         */
        public final static int NO_FACE_DETECT = 402;

        /**
         * @param SERVICE_INNER_ERROR 服务内部异常
         */
        public final static int SERVICE_INNER_ERROR = 403;

    }

    public static class DetectCode {

        //检测是否点头
        public final static int IS_NOD = 1;
        //检测是否转头
        public final static int IS_YAW = 2;
        //检测是否摇头
        public final static int IS_ROLL = 3;
        //检测是否眨眼
        public final static int IS_BLINK = 4;
        //检测是否张嘴
        public final static int IS_OPEN_MOUTH = 5;

    }

}
