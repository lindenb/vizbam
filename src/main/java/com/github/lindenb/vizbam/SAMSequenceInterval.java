package com.github.lindenb.vizbam;

import net.sf.samtools.SAMSequenceRecord;

public class SAMSequenceInterval
	{
	private SAMSequenceRecord samSequenceRecord;
	private int start;
	private int end;

	public SAMSequenceInterval(SAMSequenceRecord samSequenceRecord,int start,int end)
		{
		this.samSequenceRecord=samSequenceRecord;
		this.start=start;
		this.end=end;
		}
	
	public SAMSequenceRecord getSAMSequenceRecord()
		{
		return this.samSequenceRecord;
		}
	
	public String getName()
		{
		return getSAMSequenceRecord().getSequenceName();
		}
	
	public int getStart()
		{
		return start;
		}
	
	public int getEnd()
		{
		return end;
		}
	
	public SAMSequencePosition getLeftPosition()
		{
		return new SAMSequencePosition(getSAMSequenceRecord(), getStart());
		}
	
	public SAMSequencePosition getRightPosition()
		{
		return new SAMSequencePosition(getSAMSequenceRecord(), getEnd());
		}
	
	public SAMSequencePosition getMiddlePosition()
		{
		return new SAMSequencePosition(getSAMSequenceRecord(),(int)(((long)getStart()+(long)getEnd())/2L));
		}


	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + start;
		result = prime * result + end;
		result = prime * result + getSAMSequenceRecord().getSequenceIndex();
		return result;
		}
	
	@Override
	public boolean equals(Object obj)
		{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SAMSequenceInterval other = (SAMSequenceInterval) obj;
		return (
			start == other.start &&
		    end == other.end &&
			getName().equals(other.getName())	
			);
	}
	
	
	@Override
	public String toString() {
		return getName()+":"+getStart()+"-"+getEnd();
		}
	
}
