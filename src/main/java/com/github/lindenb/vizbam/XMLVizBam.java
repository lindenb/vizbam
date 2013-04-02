package com.github.lindenb.vizbam;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

public class XMLVizBam extends VizBam
	{
	private Document dom=null;
	public XMLVizBam(File bamFile,IndexedFastaSequenceFile ref)
		{
		super(bamFile,ref);
		}

	public XMLVizBam(SAMFileReader samFileReader,
			IndexedFastaSequenceFile indexedFastaSequenceFile)
		{
		super(samFileReader, indexedFastaSequenceFile);
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
	
	protected Element createReferenceElement()
		{
		return this.dom.createElement("reference");
		}
	private Element refElement=null;
	@Override
	protected void printReference(CharSequence reference)
		{
		this.refElement=createReferenceElement();
		this.refElement.appendChild(this.dom.createTextNode(reference.toString()));
		}
	
	
	protected Element createRulerElement()
		{
		return this.dom.createElement("ruler");
		}
	private Element rulerElement=null;
	@Override
	protected void printRuler(CharSequence ruler)
		{
		this.rulerElement=createReferenceElement();
		this.rulerElement.appendChild(this.dom.createTextNode(ruler.toString()));
		}

	
	
	private List<Element> rows=new ArrayList<Element>();
	private Element currentElement=null;
	
	protected Element createRowElement()
		{
		Element e= this.dom.createElement("row");
		e.setAttribute("index", String.valueOf(rows.size()));
		return e;
		}
	@Override
	protected void startRow()
		{
		this.currentElement=createRowElement();
		this.rows.add(this.currentElement);
		}

	@Override
	protected void endRow()
		{
		}

	
	protected Element createRecordElement(final SAMRecord record)
		{
		Element e= this.dom.createElement("record");
		return e;
		}
	
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

	
	protected Element createRecordElement(SAMRecord record, CigarElement ce)
		{
		Element e= this.dom.createElement(ce.getOperator().name());
		return e;
		}

	
	@Override
	protected void startCigar(SAMRecord record, CigarElement ce)
		{
		Element e= createRecordElement(record,ce);
		this.currentElement.appendChild(e);
		this.currentElement=e;
		}

	@Override
	protected void endCigar(SAMRecord record, CigarElement ce)
		{
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
	
	}
