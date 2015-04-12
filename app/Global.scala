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

object Global extends WithFilters(new GzipFilter(), CustomHTMLCompressorFilter(), XMLCompressorFilter()) with SecuredSettings with Logger  {

  val injector = Guice.createInjector(new GeoTrackerServiceModule)

  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)

  override def onStart(app: Application) {
  }

  override def onStop(app: Application) {
  }

  override def onNotAuthenticated(request: RequestHeader, lang: Lang): Option[Future[Result]] = {
    Some(Future.successful(Redirect(routes.ApplicationController.signIn)))
  }

  override def onNotAuthorized(request: RequestHeader, lang: Lang): Option[Future[Result]] = {
    Some(Future.successful(Redirect(routes.ApplicationController.signIn).flashing("error" -> Messages("access.denied"))))
  }
}

/**
 * Defines a user-defined HTML compressor filter.
 */
object CustomHTMLCompressorFilter {

  /**
   * Creates the HTML compressor filter.
   *
   * @return The HTML compressor filter.
   */
  def apply() = new HTMLCompressorFilter({
    val compressor = new HtmlCompressor()

    compressor.setRemoveComments(true);                                  //if false keeps HTML comments (default is true)
    compressor.setRemoveMultiSpaces(true);                               //if false keeps multiple whitespace characters (default is true)
    compressor.setRemoveIntertagSpaces(true);                            //removes iter-tag whitespace characters
    compressor.setRemoveQuotes(true);                                    //removes unnecessary tag attribute quotes
    compressor.setSimpleDoctype(true);                                   //simplify existing doctype
    compressor.setRemoveScriptAttributes(true);                          //remove optional attributes from script tags
    compressor.setRemoveStyleAttributes(true);                           //remove optional attributes from style tags
    compressor.setRemoveLinkAttributes(true);                            //remove optional attributes from link tags
    compressor.setRemoveFormAttributes(true);                            //remove optional attributes from form tags
    compressor.setRemoveInputAttributes(true);                           //remove optional attributes from input tags
    compressor.setSimpleBooleanAttributes(true);                         //remove values from boolean tag attributes
    compressor.setRemoveJavaScriptProtocol(true);                        //remove "javascript:" from inline event handlers
    compressor.setRemoveHttpProtocol(false);                             //replace "http://" with "//" inside tag attributes
    compressor.setRemoveHttpsProtocol(false);                            //replace "https://" with "//" inside tag attributes
    compressor.setPreserveLineBreaks(false);                             //preserves original line breaks
    compressor.setRemoveSurroundingSpaces("html,div,ul,ol,li,br,p,nav"); //remove spaces around provided tags
    compressor
  })
}
