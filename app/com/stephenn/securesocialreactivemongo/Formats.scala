package com.stephenn.securesocialreactivemongo

import _root_.java.util.Date
import securesocial.core._
import play.api.libs.json.Json
import securesocial.core.IdentityId
import securesocial.core.PasswordInfo
import securesocial.core.OAuth2Info
import securesocial.core.OAuth1Info
import securesocial.core.providers.Token
import org.joda.time.DateTime

object Formats {
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val AuthenticationMethodFormat = Json.format[AuthenticationMethod]
  implicit val OAuth1InfoFormat = Json.format[OAuth1Info]
  implicit val OAuth2InfoFormat = Json.format[OAuth2Info]
  implicit val PasswordInfoFormat = Json.format[PasswordInfo]
  implicit val IdentityIdFormat = Json.format[IdentityId]

  implicit val DateTimeFormat = new Format[DateTime] {
    def reads(js: JsValue) = (js \ "$date").validate[Long].map(l => new DateTime(l))
    def writes(d: DateTime) = Json.obj("$date" -> d.getMillis)
  }

  implicit val TokenFormat = Json.format[Token]

  implicit val SocialUserReads = new Reads[SocialUser] {
    def reads(js: JsValue): JsResult[SocialUser] = {
      JsSuccess(new SocialUser(
        (js \ "identityId").as[IdentityId],
        (js \ "firstName").as[String],
        (js \ "lastName").as[String],
        (js \ "fullName").as[String],
        (js \ "email").as[Option[String]],
        (js \ "avatarUrl").as[Option[String]],
        (js \ "authMethod").as[AuthenticationMethod],
        (js \ "oAuth1Info").as[Option[OAuth1Info]],
        (js \ "oAuth2Info").as[Option[OAuth2Info]],
        (js \ "passwordInfo").as[Option[PasswordInfo]]
      ))
    }
  }

  implicit val IdentityWrites = new Writes[Identity] {
    def writes(i: Identity): JsValue = {
      Json.obj(
        "identityId" -> i.identityId,
        "firstName" -> i.firstName,
        "lastName" -> i.lastName,
        "fullName" -> i.fullName,
        "email" -> i.email,
        "avatarUrl" -> i.avatarUrl,
        "authMethod" -> i.authMethod,
        "oAuth1Info" -> i.oAuth1Info,
        "oAuth2Info" -> i.oAuth2Info,
        "passwordInfo" -> i.passwordInfo
      )
    }
  }
}