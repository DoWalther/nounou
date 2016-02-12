import sbt._
import Keys._

object Build extends Build {
  import BuildSettings._
  import Dependencies._

  // configure prompt to show current project
  override lazy val settings = super.settings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Root Project
  // -------------------------------------------------------------------------------------------------------------------

  lazy val root = Project("root",file("."))
    .aggregate(core, examples, nounoufxgui)
    .settings(basicSettings: _*)


  lazy val breeze = RootProject(uri("git://github.com/ktakagaki/breeze.git#nounou"))


  // -------------------------------------------------------------------------------------------------------------------
  // Modules
  // -------------------------------------------------------------------------------------------------------------------

  lazy val core = Project("core", file("core"))
    .dependsOn( breeze )
    .settings(nounouModuleSettings: _*)
    .settings(libraryDependencies ++= backendDependencies
    )

  lazy val examples = Project("examples", file("examples"))
    .dependsOn(core, nounoufxgui)
    .settings(nounouModuleSettings: _*)
    .settings(libraryDependencies ++= backendDependencies)


  lazy val nounoufxgui = Project("nounoufxgui", file("nounoufxgui"))
    .dependsOn(core)
    .settings(nounouModuleSettings: _*)
    .settings(libraryDependencies ++= backendDependencies)

}
