import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import sbt.Keys._
import sbt._

object AutoPluginJavaAppPackaging extends AutoPlugin {
  override def trigger  = allRequirements
  override def requires = JavaAppPackaging
  override def projectSettings: Seq[Def.Setting[_]] = Seq.empty
}
