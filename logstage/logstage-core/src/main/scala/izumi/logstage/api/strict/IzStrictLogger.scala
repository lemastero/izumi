package izumi.logstage.api.strict

import izumi.logstage.api.Log.CustomContext
import izumi.logstage.api.logger.{AbstractMacroStrictLogger, LogRouter, RoutingLogger}
import izumi.logstage.api.rendering.StrictEncoded
import izumi.logstage.api.{IzLogger, IzLoggerConvenienceApi, Log}

class IzStrictLogger(
  override val router: LogRouter,
  override val customContext: Log.CustomContext,
) extends RoutingLogger
  with AbstractMacroStrictLogger {

  override def withCustomContext(context: CustomContext): IzLogger = new IzLogger(router, customContext + context)
  final def withCustomContext(context: (String, StrictEncoded)*): IzLogger = withCustomContextMap(context.toMap)
  final def withCustomContextMap(context: Map[String, StrictEncoded]): IzLogger = withCustomContext(CustomContext.fromMap(context))

  final def apply(context: CustomContext): IzLogger = withCustomContext(context)
  final def apply(context: (String, StrictEncoded)*): IzLogger = withCustomContextMap(context.toMap)
  final def apply(context: Map[String, StrictEncoded]): IzLogger = withCustomContextMap(context)

}

object IzStrictLogger extends IzLoggerConvenienceApi[IzStrictLogger] {
  override protected def make(r: LogRouter, context: CustomContext): IzStrictLogger = new IzStrictLogger(r, context)
}
