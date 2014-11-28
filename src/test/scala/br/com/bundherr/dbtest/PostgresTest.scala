package br.com.bundherr.dbtest

/**
 * Created by Rafael on 26/12/13.
 */
import br.com.bundherr.PGDriver.simple._
import java.sql.Timestamp
import br.com.bundherr.entities.Entities._
import org.scalatest.{BeforeAndAfter, Matchers, FlatSpec}
import br.com.bundherr.entities.Entities.StandardAccount

object PostgresTest {

}

class PostgresTest extends FlatSpec with Matchers {


  implicit val db = Database.forURL(url = "jdbc:postgresql://localhost:5432/bund_test", driver = "org.postgresql.Driver",user = "DBA_DDA", password = "bundherr")
  val SCHEMA_NAME = "BH_RAFAELBFS1_TESTUSR"
  val SCHEMA_NAME2 = "BH_BUNDHERR2_TESTUSR"
  //case class OsmWay(id: Int, version: Int, user_id: Int, tstamp: Timestamp, changeset_id: Int ,tags: Map[String, String], nodes: List[Int])




  db withSession { implicit session: Session =>

    val accountsQuery = new StandardAccounts
    val institutionsQuery = new Institutions
    val idHolders = new IdSpaces
    val accountSets = new AccountSets

    try {


        idHolders.ddl.create
        idHolders.forInsert.insert(IdHolder(None,"Principal","10#0000.0000"))
        idHolders.forInsert.insert(IdHolder(None,"Auxiliary","10#00.00.000"))




      val principal = (for(ids <- idHolders if ids.name === "Principal") yield ids).first()


        institutionsQuery.ddl.create
        institutionsQuery.forInsert.insert(Institution(None,10002000,principal.sequenz,"My Base"))
        institutionsQuery.forInsert.insert(Institution(None,20002000,principal.sequenz,"My Other Base"))




      val myBase = (for(ids <- institutionsQuery if ids.name === "My Base") yield ids).first()


        accountSets.ddl.create
        accountSets.forInsert.insert(AccountSet(None,"Primary",myBase.id,"16#0.0.00.00/0000"))
        accountSets.forInsert.insert(AccountSet(None,"Secondary",myBase.id,"32#0.0.0.0/000"))



      val secondary = (for(ids <- accountSets if ids.name === "Secondary") yield ids).first()


      "ID Space Principal " should "be named 'Principal'" in assert(principal.name == "Principal")


      "My Base institution" should "be called 'My Base'" in assert(myBase.name == "My Base" )
      it should "be child of 'Principal'" in (assert(myBase.idHolder==principal.sequenz))

      "Account Set fetched from Data Base" should "be named 'Secondary'" in assert( secondary.name == "Secondary")
      it should "belong to My Base" in assert(myBase.id == secondary.owner)

    /*accountsQuery.ddl create

    accountsQuery.forInsert.insert(new StandardAccount("Parent","0.0.0.0.0.0",Asset,None))
    val id = (for(sta <- accountsQuery if sta.name === "Parent" )yield sta.id).first()
    accountsQuery.forInsert.insert(new StandardAccount("Asset","1.0.0.0.0.0",Asset,Option[Long](id.get)))
    accountsQuery.forInsert.insert(new StandardAccount("Liability","2.0.0.0.0.0",Liability,Option[Long](id.get)))
    var acct = (for(sta <- accountsQuery if sta.name === "Asset" )yield sta).first()

    "Account 'Asset'" should "have name like 'Asset" in {
      assert( acct.name == "Asset")
    }
    println(acct)

    val accts = (for{p <- accountsQuery if p.parentid.isNull
                     c <- accountsQuery if c.parentid === p.id} yield (p,c)).list()

    val parent = (for(sta <- accountsQuery if sta.name === "Parent" )yield sta).first()

    "Account 'Parent'" should "have two children" in {
      assert(accts.size == 2)
    }

    it should "be parent of 'Asset" in {
      parent.id == acct.parentid
    }




      println(accts)*/
    } finally {
      accountSets.ddl.drop
      institutionsQuery.ddl.drop
      idHolders.ddl.drop
    }
  }



}

trait DataBaseBehavior { this: FlatSpec =>


}
