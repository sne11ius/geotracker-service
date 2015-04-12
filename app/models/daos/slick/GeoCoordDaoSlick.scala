package models.daos.slick

import java.util.UUID
import models.daos.UserDao
import models.User
import play.api.Play.current
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.Logger
import javax.inject.Inject
import models.GeoCoord
import models.daos.GeoCoordDao
import models.daos.slick.GeoCoordSlickDB.DBGeoCoord
import exceptions.NotFoundException
import org.joda.time.DateTime

class GeoCoordDaoSlick @Inject() (userDao: UserDao) extends GeoCoordDao {

  val slickGeoCoords = GeoCoordSlickDB.slickGeoCoords

  def save(geoCoord: GeoCoord, apiKey: UUID): GeoCoord = {
    DB withSession { implicit session =>
      userDao.findByApiKey(apiKey) match {
        case Some(user) => {
          slickGeoCoords += DBGeoCoord(
              None,
              user.userID.toString,
              geoCoord.latitude,
              geoCoord.longitude,
              geoCoord.altitude,
              geoCoord.accuracy,
              geoCoord.speed,
              geoCoord.time.getMillis
          )
          geoCoord.copy(userId = user.userID)
        }
        case None => {
          throw new NotFoundException
        }
      }
    }
  }

  def load(user: User): List[GeoCoord] = {
    DB withSession { implicit session =>
      slickGeoCoords.filter(_.userId === user.userID.toString).list.map { c =>
        GeoCoord(
          c.id,
          user.userID,
          c.latitude,
          c.longitude,
          c.altitude,
          c.accuracy,
          c.speed,
          new DateTime(c.time)
        )
      }
    }
  }

}
