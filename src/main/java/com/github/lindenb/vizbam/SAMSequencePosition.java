package com.github.lindenb.vizbam;

import net.sf.samtools.SAMSequenceRecord;

public class SAMSequencePosition
	{
	private SAMSequenceRecord samSequenceRecord;
	private int position;
	
	public SAMSequencePosition(SAMSequenceRecord samSequenceRecord,int position)
		{
		this.samSequenceRecord=samSequenceRecord;
		this.position=position;
		}
	
	public SAMSequenceRecord getSAMSequenceRecord()
		{
		return this.samSequenceRecord;
		}
	
	public String getName()
		{
		return getSAMSequenceRecord().getSequenceName();
		}
	
	public int getPosition()
		{
		return position;
		}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + position;
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
		SAMSequencePosition other = (SAMSequencePosition) obj;
		return (
			position == other.position &&
			getName().equals(other.getName())	
			);
	}
	
	
	@Override
	public String toString() {
		return getName()+":"+getPosition();
		}
	
}
