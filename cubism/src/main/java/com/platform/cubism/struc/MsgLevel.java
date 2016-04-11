package com.platform.cubism.struc;

/* 如果返回头信息是失败的，则不管是什么值，客户端将进行alert式提示,消息级别为D级
 * 如果返回头信息是成功的，则：
 * A：该类信息不显示提示
 * B：该类信息冒泡式提示，默认，前端页面以TYPE_SUCCESS风格显示
 * C：该类信息alert式警告提示，前端页面以TYPE_WARNING风格显示
 * D：该类信息alert式错误提示，前端页面以TYPE_DANGER风格显示
 */
public enum MsgLevel {
	A, B, C, D
}
