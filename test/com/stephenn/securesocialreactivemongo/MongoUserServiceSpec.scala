package com.stephenn.securesocialreactivemongo

import play.api.test.FakeApplication
import securesocial.core.providers.Token
import org.joda.time.DateTime
import play.api.libs.json.{Json, JsObject}
import scala.concurrent.{ExecutionContext, Await}
import scala.concurrent.duration._

import org.specs2.mutable._
import play.api.test.Helpers._
import scala.concurrent.duration.DurationInt
import play.api.test.FakeApplication
import play.api.libs.json.JsObject
import securesocial.core.providers.Token
import securesocial.core.{IdentityId, PasswordInfo, AuthenticationMethod, SocialUser}

class MongoUserServiceSpec extends Specification {
  import ExecutionContext.Implicits.global
  import Formats._

  "UserService" should {

    "save identities" in fakeApp { service =>
      service.save(testSocialUser)

      val list = Await.result(service.identities.find(Json.obj()).cursor[SocialUser].toList, timeout)

      list must equalTo(List(testSocialUser))
    }

    "find by identityId" in fakeApp { service =>
      service.save(testSocialUser)

      val out = service.find(testSocialUser.identityId)

      out must equalTo(Some(testSocialUser))
    }

    "find by email and provider" in fakeApp { service =>
      service.save(testSocialUser)

      val out = service.findByEmailAndProvider("1", "email")

      out must equalTo(Some(testSocialUser))
    }

    "save tokens" in fakeApp { service =>
      service.save(testToken)

      val list = Await.result(service.tokens.find(Json.obj()).cursor[Token].toList, timeout)

      list must equalTo(List(testToken))
    }

    "delete token on request" in fakeApp { service =>
      service.save(testToken)
      service.save(testToken.copy(uuid = "2"))

      service.deleteToken("1")

      val list = Await.result(service.tokens.find(Json.obj()).cursor[Token].toList, timeout)

      list must equalTo (List(testToken.copy(uuid="2")))
    }

    "delete expired tokens" in fakeApp { service =>
      service.save(testToken)
      val futureToken = testToken.copy(uuid="2", expirationTime = DateTime.now.plusMinutes(1))
      service.save(futureToken)

      service.deleteExpiredTokens()

      val list = Await.result(service.tokens.find(Json.obj()).cursor[Token].toList, timeout)

      list must equalTo (List(futureToken))
    }
  }

  val fakeApplication = FakeApplication(
    additionalConfiguration = Map("mongodb.db" -> "test"),
    additionalPlugins = List("play.modules.reactivemongo.ReactiveMongoPlugin")
  )

  def fakeApp[T](block: MongoUserService => T):T = running(fakeApplication){
    val service = new MongoUserService(play.api.Play.current)
    Await.ready(service.identities.drop(), timeout)
    Await.ready(service.tokens.drop(), timeout)

    block(service)
  }

  val timeout = DurationInt(10).seconds

  val testToken = Token(uuid ="1", email ="foo@bar.com", creationTime = new DateTime(1), expirationTime = new DateTime(2), isSignUp = false)
  val testSocialUser = SocialUser(IdentityId("1", "email"), "firstName", "lastName", "fullName", Some("email@example.com"), None, AuthenticationMethod.UserPassword, None, None, Some(PasswordInfo("bcrypt", "password", Some("salt"))))
}

