A:\��Ϸ�����/Script/npc/9900001_700.js
javax.script.ScriptException: TypeError: Cannot read property "50" from undefined in <eval> at line number 56
	at jdk.nashorn.api.scripting.NashornScriptEngine.throwAsScriptException(NashornScriptEngine.java:470)
	at jdk.nashorn.api.scripting.NashornScriptEngine.evalImpl(NashornScriptEngine.java:454)
	at jdk.nashorn.api.scripting.NashornScriptEngine.evalImpl(NashornScriptEngine.java:406)
	at jdk.nashorn.api.scripting.NashornScriptEngine.evalImpl(NashornScriptEngine.java:402)
	at jdk.nashorn.api.scripting.NashornScriptEngine.eval(NashornScriptEngine.java:150)
	at javax.script.AbstractScriptEngine.eval(Unknown Source)
	at scripting.AbstractScriptManager.getInvocable(AbstractScriptManager.java:79)
	at scripting.npc.NPCScriptManager.start(NPCScriptManager.java:104)
	at scripting.npc.NPCScriptManager.start(NPCScriptManager.java:38)
	at scripting.npc.NPCConversationManager.openNpc(NPCConversationManager.java:283)
	at jdk.nashorn.internal.scripts.Script$Recompilation$673600$217AAA$\^eval\_.action(<eval>:94)
	at scripting.npc.NPCScript$$NashornJavaAdapter.action(Unknown Source)
	at scripting.npc.NPCScriptManager.action(NPCScriptManager.java:158)
	at handling.channel.handler.NPCHandler.NPCMoreTalk(NPCHandler.java:126)
	at packet.netty.ChannelHandler.channelRead(ChannelHandler.java:170)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348)
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340)
	at io.netty.handler.codec.ByteToMessageDecoder.fireChannelRead(ByteToMessageDecoder.java:310)
	at io.netty.handler.codec.ByteToMessageDecoder.channelRead(ByteToMessageDecoder.java:284)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348)
	at io.netty.channel.AbstractChannelHandlerContext.fireChannelRead(AbstractChannelHandlerContext.java:340)
	at io.netty.channel.DefaultChannelPipeline$HeadContext.channelRead(DefaultChannelPipeline.java:1434)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:362)
	at io.netty.channel.AbstractChannelHandlerContext.invokeChannelRead(AbstractChannelHandlerContext.java:348)
	at io.netty.channel.DefaultChannelPipeline.fireChannelRead(DefaultChannelPipeline.java:965)
	at io.netty.channel.nio.AbstractNioByteChannel$NioByteUnsafe.read(AbstractNioByteChannel.java:163)
	at io.netty.channel.nio.NioEventLoop.processSelectedKey(NioEventLoop.java:647)
	at io.netty.channel.nio.NioEventLoop.processSelectedKeysOptimized(NioEventLoop.java:582)
	at io.netty.channel.nio.NioEventLoop.processSelectedKeys(NioEventLoop.java:499)
	at io.netty.channel.nio.NioEventLoop.run(NioEventLoop.java:461)
	at io.netty.util.concurrent.SingleThreadEventExecutor$5.run(SingleThreadEventExecutor.java:884)
	at io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)
	at java.lang.Thread.run(Unknown Source)
Caused by: <eval>:56 TypeError: Cannot read property "50" from undefined
	at jdk.nashorn.internal.runtime.ECMAErrors.error(ECMAErrors.java:57)
	at jdk.nashorn.internal.runtime.ECMAErrors.typeError(ECMAErrors.java:213)
	at jdk.nashorn.internal.runtime.ECMAErrors.typeError(ECMAErrors.java:185)
	at jdk.nashorn.internal.runtime.ECMAErrors.typeError(ECMAErrors.java:172)
	at jdk.nashorn.internal.runtime.Undefined.get(Undefined.java:157)
	at jdk.nashorn.internal.scripts.Script$673629$\^eval\_.:program(<eval>:56)
	at jdk.nashorn.internal.runtime.ScriptFunctionData.invoke(ScriptFunctionData.java:637)
	at jdk.nashorn.internal.runtime.ScriptFunction.invoke(ScriptFunction.java:494)
	at jdk.nashorn.internal.runtime.ScriptRuntime.apply(ScriptRuntime.java:393)
	at jdk.nashorn.api.scripting.NashornScriptEngine.evalImpl(NashornScriptEngine.java:449)
	... 33 more

---------------------------------