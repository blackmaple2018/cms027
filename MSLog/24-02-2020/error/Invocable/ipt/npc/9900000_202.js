A:\��Ϸ�����/Script/npc/9900000_202.js
javax.script.ScriptException: <eval>:34:12 Expected ; but found ��
				�ı� += i"��"+ii+" �� "+��ֵ���(cm.getPlayer().getWorldId(),ii)+"\r\n";
				        ^ in <eval> at line number 34 at column number 12
	at jdk.nashorn.api.scripting.NashornScriptEngine.throwAsScriptException(NashornScriptEngine.java:470)
	at jdk.nashorn.api.scripting.NashornScriptEngine.compileImpl(NashornScriptEngine.java:537)
	at jdk.nashorn.api.scripting.NashornScriptEngine.compileImpl(NashornScriptEngine.java:524)
	at jdk.nashorn.api.scripting.NashornScriptEngine.evalImpl(NashornScriptEngine.java:402)
	at jdk.nashorn.api.scripting.NashornScriptEngine.eval(NashornScriptEngine.java:150)
	at javax.script.AbstractScriptEngine.eval(Unknown Source)
	at scripting.AbstractScriptManager.getInvocable(AbstractScriptManager.java:79)
	at scripting.npc.NPCScriptManager.start(NPCScriptManager.java:104)
	at scripting.npc.NPCScriptManager.start(NPCScriptManager.java:38)
	at scripting.npc.NPCConversationManager.openNpc(NPCConversationManager.java:283)
	at jdk.nashorn.internal.scripts.Script$Recompilation$625254$1159AAA$\^eval\_.action(<eval>:302)
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
Caused by: jdk.nashorn.internal.runtime.ParserException: <eval>:34:12 Expected ; but found ��
				�ı� += i"��"+ii+" �� "+��ֵ���(cm.getPlayer().getWorldId(),ii)+"\r\n";
				        ^
	at jdk.nashorn.internal.parser.AbstractParser.error(AbstractParser.java:294)
	at jdk.nashorn.internal.parser.AbstractParser.error(AbstractParser.java:279)
	at jdk.nashorn.internal.parser.AbstractParser.expectDontAdvance(AbstractParser.java:350)
	at jdk.nashorn.internal.parser.AbstractParser.expect(AbstractParser.java:337)
	at jdk.nashorn.internal.parser.Parser.endOfLine(Parser.java:3372)
	at jdk.nashorn.internal.parser.Parser.expressionStatement(Parser.java:1160)
	at jdk.nashorn.internal.parser.Parser.statement(Parser.java:967)
	at jdk.nashorn.internal.parser.Parser.statement(Parser.java:863)
	at jdk.nashorn.internal.parser.Parser.statementList(Parser.java:1013)
	at jdk.nashorn.internal.parser.Parser.getBlock(Parser.java:531)
	at jdk.nashorn.internal.parser.Parser.getStatement(Parser.java:555)
	at jdk.nashorn.internal.parser.Parser.forStatement(Parser.java:1325)
	at jdk.nashorn.internal.parser.Parser.statement(Parser.java:893)
	at jdk.nashorn.internal.parser.Parser.statement(Parser.java:863)
	at jdk.nashorn.internal.parser.Parser.statementList(Parser.java:1013)
	at jdk.nashorn.internal.parser.Parser.getBlock(Parser.java:531)
	at jdk.nashorn.internal.parser.Parser.getStatement(Parser.java:555)
	at jdk.nashorn.internal.parser.Parser.forStatement(Parser.java:1325)
	at jdk.nashorn.internal.parser.Parser.statement(Parser.java:893)
	at jdk.nashorn.internal.parser.Parser.statement(Parser.java:863)
	at jdk.nashorn.internal.parser.Parser.statementList(Parser.java:1013)
	at jdk.nashorn.internal.parser.Parser.getBlock(Parser.java:531)
	at jdk.nashorn.internal.parser.Parser.getStatement(Parser.java:555)
	at jdk.nashorn.internal.parser.Parser.ifStatement(Parser.java:1187)
	at jdk.nashorn.internal.parser.Parser.statement(Parser.java:890)
	at jdk.nashorn.internal.parser.Parser.sourceElements(Parser.java:773)
	at jdk.nashorn.internal.parser.Parser.functionBody(Parser.java:2901)
	at jdk.nashorn.internal.parser.Parser.functionExpression(Parser.java:2663)
	at jdk.nashorn.internal.parser.Parser.statement(Parser.java:875)
	at jdk.nashorn.internal.parser.Parser.sourceElements(Parser.java:773)
	at jdk.nashorn.internal.parser.Parser.program(Parser.java:709)
	at jdk.nashorn.internal.parser.Parser.parse(Parser.java:283)
	at jdk.nashorn.internal.parser.Parser.parse(Parser.java:249)
	at jdk.nashorn.internal.runtime.Context.compile(Context.java:1284)
	at jdk.nashorn.internal.runtime.Context.compileScript(Context.java:1251)
	at jdk.nashorn.internal.runtime.Context.compileScript(Context.java:627)
	at jdk.nashorn.api.scripting.NashornScriptEngine.compileImpl(NashornScriptEngine.java:535)
	... 33 more

---------------------------------
A:\��Ϸ�����/Script/npc/9900000_202.js
javax.script.ScriptException: <eval>:99:21 Missing space after numeric literal
    return count*0.97f;
                     ^ in <eval> at line number 99 at column number 21
	at jdk.nashorn.api.scripting.NashornScriptEngine.throwAsScriptException(NashornScriptEngine.java:470)
	at jdk.nashorn.api.scripting.NashornScriptEngine.compileImpl(NashornScriptEngine.java:537)
	at jdk.nashorn.api.scripting.NashornScriptEngine.compileImpl(NashornScriptEngine.java:524)
	at jdk.nashorn.api.scripting.NashornScriptEngine.evalImpl(NashornScriptEngine.java:402)
	at jdk.nashorn.api.scripting.NashornScriptEngine.eval(NashornScriptEngine.java:150)
	at javax.script.AbstractScriptEngine.eval(Unknown Source)
	at scripting.AbstractScriptManager.getInvocable(AbstractScriptManager.java:79)
	at scripting.npc.NPCScriptManager.start(NPCScriptManager.java:104)
	at scripting.npc.NPCScriptManager.start(NPCScriptManager.java:38)
	at scripting.npc.NPCConversationManager.openNpc(NPCConversationManager.java:283)
	at jdk.nashorn.internal.scripts.Script$Recompilation$630691$1159AAA$\^eval\_.action(<eval>:302)
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
Caused by: jdk.nashorn.internal.runtime.ParserException: <eval>:99:21 Missing space after numeric literal
    return count*0.97f;
                     ^
	at jdk.nashorn.internal.parser.Lexer.error(Lexer.java:1700)
	at jdk.nashorn.internal.parser.Lexer.scanNumber(Lexer.java:1135)
	at jdk.nashorn.internal.parser.Lexer.lexify(Lexer.java:1614)
	at jdk.nashorn.internal.parser.AbstractParser.getToken(AbstractParser.java:132)
	at jdk.nashorn.internal.parser.AbstractParser.T(AbstractParser.java:147)
	at jdk.nashorn.internal.parser.Parser.statement(Parser.java:947)
	at jdk.nashorn.internal.parser.Parser.sourceElements(Parser.java:773)
	at jdk.nashorn.internal.parser.Parser.functionBody(Parser.java:2901)
	at jdk.nashorn.internal.parser.Parser.functionExpression(Parser.java:2663)
	at jdk.nashorn.internal.parser.Parser.statement(Parser.java:875)
	at jdk.nashorn.internal.parser.Parser.sourceElements(Parser.java:773)
	at jdk.nashorn.internal.parser.Parser.program(Parser.java:709)
	at jdk.nashorn.internal.parser.Parser.parse(Parser.java:283)
	at jdk.nashorn.internal.parser.Parser.parse(Parser.java:249)
	at jdk.nashorn.internal.runtime.Context.compile(Context.java:1284)
	at jdk.nashorn.internal.runtime.Context.compileScript(Context.java:1251)
	at jdk.nashorn.internal.runtime.Context.compileScript(Context.java:627)
	at jdk.nashorn.api.scripting.NashornScriptEngine.compileImpl(NashornScriptEngine.java:535)
	... 33 more

---------------------------------