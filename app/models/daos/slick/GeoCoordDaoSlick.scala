package models.daos.slick

import java.util.UUID
import models.daos.UserDao
import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import play.api.Play.current
import scala.concurrent.Future
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import models.daos.PasswordInfoDao
import play.api.Logger
import javax.inject.Inject

import models.GeoCoord
import models.daos.GeoCoordDao
import models.daos.slick.GeoCoordSlickDB.DBGeoCoord
import exceptions.NotFoundException

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

}
