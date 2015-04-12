package models.daos.slick

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import play.api.Play.current
import scala.concurrent.Future
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import models.daos.PasswordInfoDao
import play.api.Logger

import models.daos.slick.PasswordInfoSlickDB.DBPasswordInfo

class PasswordInfoDaoSlick extends PasswordInfoDao {

  val slickLoginInfos = LoginInfoSlickDB.slickLoginInfos
  val slickPasswordInfos = PasswordInfoSlickDB.slickPasswordInfos

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = {
    Future.successful {
      DB withSession {implicit session =>
        val infoId = slickLoginInfos.filter(
          x => x.providerID === loginInfo.providerID && x.providerKey === loginInfo.providerKey
        ).first.id.get
        slickPasswordInfos insert DBPasswordInfo(authInfo.hasher, authInfo.password, authInfo.salt, infoId)
        authInfo
      }
    }
  }

  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = {
    Future.successful {
      DB withSession { implicit session =>
        slickLoginInfos.filter(info => info.providerID === loginInfo.providerID && info.providerKey === loginInfo.providerKey).firstOption match {
          case Some(info) =>
            val passwordInfo = slickPasswordInfos.filter(_.loginInfoId === info.id).first
            val result = PasswordInfo(passwordInfo.hasher, passwordInfo.password, passwordInfo.salt)
            Logger.debug(s"No PasswordInfo for loginInfo ${loginInfo}: ${result}")
            Some(result)
          case None => {
            Logger.debug(s"No PasswordInfo for loginInfo ${loginInfo}")
            None
          }
        }
      }
    }
  }
}
