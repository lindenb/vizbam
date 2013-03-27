package com.github.lindenb.vizbam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.samtools.SAMRecord;

public class Consensus
	{
	private static class BaseAndQual
		{
		byte base;
		int qual;
		BaseAndQual(byte base,int qual)
			{
			this.base=base;
			this.qual=qual;
			}
		}
	private Character referenceBase=null;
	private List<BaseAndQual> column=new ArrayList<Consensus.BaseAndQual>();
	
	public Consensus()
		{
		
		}
	
	public Character getReferenceBase()
		{
		return referenceBase;
		}
	
	public void setReferenceBase(char referenceBase)
		{
		this.referenceBase = referenceBase;
		}
	
	public void add(final SAMRecord record,int index)
		{
		column.add(new BaseAndQual(
				record.getReadBases()[index],
				record.getBaseQualities()[index]
				));
		}
	
	/* TODO */
	public char getConsensus()
		{
		return '?';
		}
	
	}
