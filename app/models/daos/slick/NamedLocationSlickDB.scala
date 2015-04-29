package models.daos.slick

import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime

object NamedLocationSlickDB {

  case class DBNamedLocation (
    id: Option[Long],
    userId: String,
    name: String,
    latitude: Double,
    longitude: Double,
    radius: Float
  )

  class NamedLocations(tag: Tag) extends Table[DBNamedLocation](tag, "namedlocations") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[String]("userId")
    def name = column[String]("name")
    def latitude = column[Double]("latitude")
    def longitude = column[Double]("longitude")
    def radius = column[Float]("radius")
    def * = (id.?, userId, name, latitude, longitude, radius) <> (DBNamedLocation.tupled, DBNamedLocation.unapply)
  }

  val slickNamedLocations = TableQuery[NamedLocations]

}
