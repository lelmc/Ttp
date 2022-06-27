# Ttp
权限：ttp.admin

命令：/ttp 玩家ID 世界名称 x y z 返回时间
传送玩家到指定的世界坐标  规定多久后返航(单位：秒

配置文件：
config/ttp/玩家UUID.json
用来保存玩家返航的位置（
为什么保存？
防止服务器崩溃玩家没有返航

当玩家warp tppos 发生传送移动时自动取消返航
