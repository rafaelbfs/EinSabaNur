package br.com.bundherr


/**
 * Created by Rafael on 25/12/13.
 */

import slick.driver.PostgresDriver
import com.github.tminglei.slickpg._



trait PGDriver extends PostgresDriver
with PgArraySupport
with PgDateSupportJoda
with PgRangeSupport
with PgJsonSupport
with PgHStoreSupport
with PgSearchSupport
with PgPostGISSupport {
  /// for json support
  type DOCType = text.Document
  override val jsonMethods = org.json4s.native.JsonMethods

  ///
  override val Implicit = new ImplicitsPlus {}
  override val simple = new SimpleQLPlus {}

  //////
  trait ImplicitsPlus extends Implicits
  with ArrayImplicits
  with DateTimeImplicits
  with RangeImplicits
  with HStoreImplicits
  with JsonImplicits
  with SearchImplicits
  with PostGISImplicits

  trait SimpleQLPlus extends SimpleQL
  with ImplicitsPlus
  with SearchAssistants
}


object PGDriver extends PGDriver

