package com.github.lindenb.vizbam;

import java.io.File;
import java.io.PrintWriter;

import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

public class TTYVizBam extends VizBam
	{
	private PrintWriter out=new PrintWriter(System.out);
	public TTYVizBam(File bamFile,
			ReferenceSequenceFile indexedFastaSequenceFile)
		{
		super(bamFile, indexedFastaSequenceFile);
		}

	public TTYVizBam(SAMFileReader samFileReader,
			ReferenceSequenceFile indexedFastaSequenceFile)
		{
		super(samFileReader, indexedFastaSequenceFile);
		}
	
	
	public void setWriter(PrintWriter out)
		{
		this.out = out;
		}
	
	public PrintWriter getWriter()
		{
		return out;
		}
	

	@Override
	protected void startAlign(String chromName, int position)
		{
		
		}

	@Override
	protected void endAlign(String chromName, int position)
		{
		
		}
	
	@Override
	protected void startBase(char base, int qual) {
		
		}

	@Override
	protected void endBase() {
		
		}
	
	@Override
	protected void printReference(CharSequence reference)
		{
		getWriter().println(reference);
		
		}
	@Override
	protected void printRuler(CharSequence ruler)
		{
		getWriter().println(ruler);
		}


	@Override
	protected void startRow()
		{
		}

	@Override
	protected void endRow()
		{
		getWriter().println();
		}

	@Override
	protected void startSamRecord(SAMRecord record)
		{
		}

	@Override
	protected void endSamRecord(SAMRecord record)
		{
		}

	@Override
	protected void startCigar(SAMRecord record, CigarElement ce)
		{		
		}

	@Override
	protected void endCigar(SAMRecord record, CigarElement ce)
		{		
		}

	@Override
	protected void write(char c)
		{
		getWriter().print(c);
		}
	@Override
	public void close() {
		getWriter().flush();
		super.close();
		}
	

	}
