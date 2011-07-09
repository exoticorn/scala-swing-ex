import swing._
import java.awt.datatransfer.{DataFlavor, Transferable}
import javax.swing.{TransferHandler, JComponent}

class FlavoredData(val flavor: DataFlavor, val data: Any)

object FlavoredData {
	def apply(flavor: DataFlavor, data: Any) = new FlavoredData(flavor, data)
	def unapply(f: FlavoredData) = Some((f.flavor, f.data))
}

class MyTransferHandler(fnc: PartialFunction[FlavoredData, Unit]) extends TransferHandler {
	override def canImport(component: JComponent, flavors: Array[DataFlavor]) =
		flavors exists { f => fnc.isDefinedAt(FlavoredData(f, null)) }
	
	override def importData(component: JComponent, t: Transferable) = t.getTransferDataFlavors() exists { f =>
		if(fnc.isDefinedAt(FlavoredData(f, null))) {
			fnc(FlavoredData(f, t.getTransferData(f)))
			true
		} else false
	}
}

object MyTransferHandler {
	def apply(fnc: PartialFunction[FlavoredData, Unit]) = new MyTransferHandler(fnc)
}

object TestApp extends SimpleSwingApplication {
	def top = new MainFrame {
		title = "Test App"
		contents = new Label("Hello, World!") {
			preferredSize = new java.awt.Dimension(400, 200)
			peer.setTransferHandler(MyTransferHandler {
				case FlavoredData(DataFlavor.stringFlavor, data) => println(data)
			})
		}
	}
}

