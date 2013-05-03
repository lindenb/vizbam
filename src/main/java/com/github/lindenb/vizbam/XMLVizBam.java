package com.github.lindenb.vizbam;

import java.io.File;

import org.w3c.dom.Element;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

public class XMLVizBam extends AbstractXMLVizBam
	{
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
	protected Element createReferenceElement()
		{
		return getDocument().createElement("reference");
		}

	
	
	protected Element createRulerElement()
		{
		return  getDocument().createElement("ruler");
		}
	
	
	@Override
	protected Element createRowElement(int rowIndex)
		{
		Element e= getDocument().createElement("row");
		e.setAttribute("index", String.valueOf(rowIndex));
		return e;
		}
	
	@Override
	protected Element createRecordElement(final SAMRecord record)
		{
		Element e= getDocument().createElement("record");
		return e;
		}
	
	@Override
	protected Element createCigarOperatorElement(SAMRecord record,
			CigarElement ce)
		{
		Element e= getDocument().createElement(ce.getOperator().name());
		return e;
		}

	
	
	}
