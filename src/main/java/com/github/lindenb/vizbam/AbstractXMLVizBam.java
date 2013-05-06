package com.github.lindenb.vizbam;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

public abstract class AbstractXMLVizBam extends VizBam
	{
	private Document dom=null;
	protected AbstractXMLVizBam(File bamFile,IndexedFastaSequenceFile ref)
		{
		super(bamFile,ref);
		}

	public AbstractXMLVizBam(SAMFileReader samFileReader,
			IndexedFastaSequenceFile indexedFastaSequenceFile)
		{
		super(samFileReader, indexedFastaSequenceFile);
		}
	
	public Document getDocument()
		{
		return dom;
		}
	
	
	@Override
	protected void cleanup()
		{
		super.cleanup();
		dom=null;
		this.refElement=null;
		this.rulerElement=null;
		this.rows.clear();
		}
	
	protected boolean isNamespaceAware()
		{
		return false;
		}
	
	protected Document createDocument()
			{
			try {
				DocumentBuilderFactory f=DocumentBuilderFactory.newInstance();
				f.setCoalescing(true);
				f.setNamespaceAware(this.isNamespaceAware());
				f.setValidating(false);
				f.setExpandEntityReferences(true);
				f.setIgnoringComments(false);
				DocumentBuilder docBuilder= f.newDocumentBuilder();
				return docBuilder.newDocument();
			} catch (ParserConfigurationException e)
				{
				throw new RuntimeException(e);
				}
			}
	@Override
	protected void startAlign(String chromName, int position)
		{
		this.dom=createDocument();
		
		}

	@Override
	protected void endAlign(String chromName, int position)
		{
		
		}
	
	protected abstract Element createReferenceElement();
	
	private Element refElement=null;
	@Override
	protected void printReference(CharSequence reference)
		{
		this.refElement=createReferenceElement();
		this.refElement.appendChild(this.dom.createTextNode(reference.toString()));
		}
	
	
	protected abstract Element createRulerElement();
		
	private Element rulerElement=null;
	@Override
	protected void printRuler(CharSequence ruler)
		{
		this.rulerElement=createReferenceElement();
		this.rulerElement.appendChild(this.dom.createTextNode(ruler.toString()));
		}

	
	
	private List<Element> rows=new ArrayList<Element>();
	private Element currentElement=null;
	
	protected abstract Element createRowElement(int rowIndex);
		
	@Override
	protected void startRow()
		{
		this.currentElement=createRowElement(this.rows.size());
		this.rows.add((Element)this.currentElement);
		}

	@Override
	protected void endRow()
		{
		}

	
	protected abstract Element createRecordElement(final SAMRecord record);
	
	@Override
	protected void startSamRecord(final SAMRecord record)
		{
		this.currentElement=createRecordElement(record);
		this.rows.get(this.rows.size()-1).appendChild(this.currentElement);
		}

	@Override
	protected void endSamRecord(SAMRecord record)
		{
		this.currentElement=(Element)this.currentElement.getParentNode();
		}

	
	protected abstract Element createCigarOperatorElement(SAMRecord record, CigarElement ce);

	
	@Override
	protected void startCigar(SAMRecord record, CigarElement ce)
		{
		Element e= createCigarOperatorElement(record,ce);
		this.currentElement.appendChild(e);
		this.currentElement=e;
		}
	
	@Override
	protected void endCigar(SAMRecord record, CigarElement ce)
		{
		this.currentElement=(Element)this.currentElement.getParentNode();		
		}

	protected abstract Element createBaseElement(char c,int qual);
	
	
	@Override
	protected void startBase(char base, int qual)
		{

		Element e= createBaseElement(base,qual);
		this.currentElement.appendChild(e);
		this.currentElement=e;
		}
	
	@Override
	protected void endBase() {
		this.currentElement=(Element)this.currentElement.getParentNode();		
		}
	
	@Override
	protected void write(char c)
		{
		Node last=this.currentElement.getLastChild();
		if(last==null || last.getNodeType()!=Node.TEXT_NODE)
			{
			this.currentElement.appendChild(this.dom.createTextNode(""+c));
			}
		else
			{
			Text text=(Text)last;
			this.currentElement.replaceChild(
					this.dom.createTextNode(text.getData()+c),
					last);
			}
		}
	
	public void print(Writer out) throws IOException
		{
		  try {
			  DocumentFragment f=getDocument().createDocumentFragment();
			  if(refElement!=null) f.appendChild(this.refElement);
			  if(rulerElement!=null) f.appendChild(this.rulerElement);
			  
			  for(Element r:this.rows) f.appendChild(r);
			  TransformerFactory tFactory =  TransformerFactory.newInstance();
			  Transformer transformer = tFactory.newTransformer();
			  transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			  DOMSource source = new DOMSource(f);
			  StreamResult result = new StreamResult(out);
			  transformer.transform(source, result);
			  out.flush();
		} catch (Exception e) {
			throw new IOException(e);
		} 


		}
	
	}
