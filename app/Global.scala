import com.google.inject.Guice
import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter
import com.mohiva.play.xmlcompressor.XMLCompressorFilter
import play.api.Application
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc.WithFilters
import play.filters.gzip.GzipFilter
import util.GeoTrackerServiceModule
import java.util.Locale
import com.mohiva.play.silhouette.api.{ Logger, SecuredSettings }
import play.api.mvc.Results._
import play.api.mvc.{ RequestHeader, Result }
import play.api.i18n.{ Lang, Messages }
import controllers.routes

import scala.concurrent.Future

object Global extends WithFilters(new GzipFilter()) with SecuredSettings with Logger  {

  val injector = Guice.createInjector(new GeoTrackerServiceModule)

  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)

  override def onStart(app: Application) {
  }

  override def onStop(app: Application) {
  }

  override def onNotAuthenticated(request: RequestHeader, lang: Lang): Option[Future[Result]] = {
    super.onNotAuthenticated(request, lang)
  }

  override def onNotAuthorized(request: RequestHeader, lang: Lang): Option[Future[Result]] = {
    super.onNotAuthorized(request, lang)
  }
}
