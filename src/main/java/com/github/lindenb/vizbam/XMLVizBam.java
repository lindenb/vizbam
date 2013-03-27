package com.github.lindenb.vizbam;

import java.io.File;

import net.sf.picard.reference.FastaSequenceFile;
import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.samtools.SAMRecordIterator;

public class XMLVizBam extends VizBam
	{
	public XMLVizBam(File bamFile,IndexedFastaSequenceFile ref)
		{
		super(bamFile,ref);
		}
	
	}
