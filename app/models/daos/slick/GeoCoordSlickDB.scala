package models.daos.slick

import play.api.db.slick.Config.driver.simple._
import org.joda.time.DateTime

object GeoCoordSlickDB {

  case class DBGeoCoord (
    id: Option[Long],
    userId: String,
    latitude: Double,
    longitude: Double,
    altitude: Double,
    accuracy: Float,
    speed: Float,
    time: Long
  )

  class GeoCoords(tag: Tag) extends Table[DBGeoCoord](tag, "geocoords") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def userId = column[String]("userId")
    def latitude = column[Double]("latitude")
    def longitude = column[Double]("longitude")
    def altitude = column[Double]("altitude")
    def accuracy = column[Float]("accuracy")
    def speed = column[Float]("speed")
    def time = column[Long]("time")
    def * = (id.?, userId, latitude, longitude, altitude, accuracy, speed, time) <> (DBGeoCoord.tupled, DBGeoCoord.unapply)
  }

  val slickGeoCoords = TableQuery[GeoCoords]

}
