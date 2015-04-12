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

import models.daos.slick.UserSlickDB.DBUser
import models.daos.slick.LoginInfoSlickDB.DBLoginInfo
import models.daos.slick.UserLoginInfoSlickDB.DBUserLoginInfo

class UserDaoSlick extends UserDao {

  val slickLoginInfos = LoginInfoSlickDB.slickLoginInfos
  val slickUsers = UserSlickDB.slickUsers
  val slickUserLoginInfos = UserLoginInfoSlickDB.slickUserLoginInfos

  def find(loginInfo: LoginInfo): Future[Option[User]] = {
        DB withSession { implicit session =>
      Future.successful {
        slickLoginInfos.filter(
          x => x.providerID === loginInfo.providerID && x.providerKey === loginInfo.providerKey
        ).firstOption match {
          case Some(info) =>
            slickUserLoginInfos.filter(_.loginInfoId === info.id).firstOption match {
              case Some(userLoginInfo) =>
                slickUsers.filter(_.id === userLoginInfo.userID).firstOption match {
                  case Some(user) =>
                    Some(User(UUID.fromString(user.userID), UUID.fromString(user.apiKey), loginInfo, user.firstName, user.lastName, user.fullName, user.email, user.avatarURL))
                  case None => None
                }
              case None => None
            }
          case None => None
        }
      }
    }
  }

  def find(userID: UUID): Future[Option[User]] = {
        DB withSession { implicit session =>
          throw new NotImplementedError
    }
  }

  def findByApiKey(apiKey: UUID): Option[User] = {
    DB withSession { implicit session =>
      slickUsers.filter(_.apiKey === apiKey.toString()).firstOption match {
        case Some(user) =>
          slickUserLoginInfos.filter(_.userID === user.userID).firstOption match {
            case Some(userLoginInfo) => {
              slickLoginInfos.filter(_.id === userLoginInfo.loginInfoId).firstOption match {
                case Some(dbLoginInfo) => {
                  val loginInfo = LoginInfo(dbLoginInfo.providerID, dbLoginInfo.providerKey)
                	Some(User(UUID.fromString(user.userID), UUID.fromString(user.apiKey), loginInfo, user.firstName, user.lastName, user.fullName, user.email, user.avatarURL))
                }
                case None => None
              }
            }
            case None => None
          }
        case None => None
      }
    }
  }

  def save(user: User): Future[User] = {
    DB withSession { implicit session =>
      Future.successful {
        val dbUser = DBUser(user.userID.toString, user.apiKey.toString, user.firstName, user.lastName, user.fullName, user.email, user.avatarURL)
        slickUsers.filter(_.id === dbUser.userID).firstOption match {
          case Some(userFound) => slickUsers.filter(_.id === dbUser.userID).update(dbUser)
          case None => slickUsers.insert(dbUser)
        }
        var dbLoginInfo = DBLoginInfo(None, user.loginInfo.providerID, user.loginInfo.providerKey)
        // Insert if it does not exist yet
        slickLoginInfos.filter(info => info.providerID === dbLoginInfo.providerID && info.providerKey === dbLoginInfo.providerKey).firstOption match {
          case None => slickLoginInfos.insert(dbLoginInfo)
          case Some(info) => Logger.debug("Nothing to insert since info already exists: " + info)
        }
        dbLoginInfo = slickLoginInfos.filter(info => info.providerID === dbLoginInfo.providerID && info.providerKey === dbLoginInfo.providerKey).first
        // Now make sure they are connected
        slickUserLoginInfos.filter(info => info.userID === dbUser.userID && info.loginInfoId === dbLoginInfo.id).firstOption match {
          case Some(info) =>
            // They are connected already, we could as well omit this case ;)
          case None =>
            slickUserLoginInfos += DBUserLoginInfo(dbUser.userID, dbLoginInfo.id.get)
        }
        user // We do not change the user => return it
      }
    }
  }
}
