package forms

import play.api.data.Form
import play.api.data.Forms._

object CreateLocationForm {

  val form = Form(
    mapping(
      "name" -> nonEmptyText,
      "latitude" -> nonEmptyText,
      "longitude" -> nonEmptyText,
      "radius" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  case class Data(
    name: String,
    latitude: String,
    longitude: String,
    radius: String)
}
