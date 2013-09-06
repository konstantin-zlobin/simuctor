package ru.zconstz.simuctor

import akka.actor._

object SimuctorApplication extends App {

  implicit val actorSystem = ActorSystem("simuctor-actor-system")

  val game = ActorDSL.actor(new Game)

  game.tell(InitGame, ActorDSL.actor(new Actor {
    def receive = {
      case GameInitialized => {
        println("Game is ready")
        game ! GameTick

        Thread.sleep(1000)
        game ! FinishGame
      }
    }
  }))

  actorSystem.shutdown()
}
