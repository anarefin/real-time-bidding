package com.arefin

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.arefin.entity.CampaignData
import com.arefin.routes.{BiddingRoute}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.io.StdIn
import scala.util.{Failure, Success}

object AkkaHttpServer extends App with LazyLogging{

  implicit val system = ActorSystem("real-time-bid-server")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec = system.dispatcher


  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")


  implicit val globalRejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case ValidationRejection(msg, route) =>
        complete(StatusCodes.InternalServerError, s"The operation is not supported, error = $msg")
      }
      .handleNotFound {
        complete(StatusCodes.NotFound, "The path is not supported.")
      }
      .result()


  implicit val globalExceptionHandler = ExceptionHandler {
    case e: RuntimeException => complete(s"A runtime exception occurred with, msg = ${e.getMessage}")
  }


//  val serverVersion = new ServerVersion()
//  val serverVersionRoute = serverVersion.route()
//  val serverVersionRouteAsJson = serverVersion.routeAsJson()
//  val serverVersionJsonEncoding = serverVersion.routeAsJsonEncoding()
//  val donutRoutes = new DonutRoutes().route()
  val biddingRoutes = new BiddingRoute().route()

  val routes: Route =  biddingRoutes
  // ~ serverUpRoute

  val httpServerFuture = Http().bindAndHandle(routes, host, port)
  httpServerFuture.onComplete {
    case Success(binding) =>
      logger.info(s"Akka Http Server is UP and is bound to ${binding.localAddress}")

    case Failure(e) =>
      logger.error(s"Akka Http server failed to start", e)
      system.terminate()
  }

  StdIn.readLine() // let it run until user presses return
  httpServerFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
