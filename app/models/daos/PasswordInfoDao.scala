package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO

import scala.collection.mutable
import scala.concurrent.Future

trait PasswordInfoDao extends DelegableAuthInfoDAO[PasswordInfo] {

  def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo]

  def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]]

}
