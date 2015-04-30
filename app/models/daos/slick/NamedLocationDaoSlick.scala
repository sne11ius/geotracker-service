package models.daos.slick

import models.User
import models.GeoCoord
import play.api.Play.current
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.Logger
import javax.inject.Inject
import models.daos.NamedLocationDao
import models.NamedLocation
import models.daos.slick.NamedLocationSlickDB.DBNamedLocation
import org.joda.time.Interval

class NamedLocationDaoSlick @Inject() () extends NamedLocationDao {

  val slickNamedLocations = NamedLocationSlickDB.slickNamedLocations
  val slickGeoCoords = GeoCoordSlickDB.slickGeoCoords

  override def addLocation(l: NamedLocation, user: User) = {
    DB withSession { implicit session =>
      slickNamedLocations += DBNamedLocation(None, user.userID.toString, l.name, l.latitude, l.longitude, l.radius)
    }
  }

  override def loadLocations(user: User): List[NamedLocation] = {
    DB withSession { implicit session =>
      slickNamedLocations.filter { _.userId === user.userID.toString }.list.map {
        l => NamedLocation(l.id, l.name, l.latitude, l.longitude, l.radius)
      }
    }
  }

  override def find(user: User, locationId: Long): Option[NamedLocation] = {
    DB withSession { implicit session =>
      slickNamedLocations.filter {_.id === locationId }.firstOption match {
        case None => None
        case Some(l) => {
          if (l.userId == user.userID.toString) {
            Some(NamedLocation(l.id, l.name, l.latitude, l.longitude, l.radius))
          } else {
            None
          }
        }
      }
    }
  }

}
