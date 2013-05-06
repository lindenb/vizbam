package com.github.lindenb.vizbam;

import java.util.ArrayList;
import java.util.List;

import net.sf.samtools.SAMRecord;

public class PileupSamRecordListPacker extends AbstractSamRecordListpacker
	{
	@Override
	public List<List<SAMRecord>> pack(List<SAMRecord> records) {
		List<SAMRecord> copy=new ArrayList<SAMRecord>(records);
		sortSamRecords(copy);
		List<List<SAMRecord>> rows=new ArrayList<List<SAMRecord>>(copy.size());
		for(SAMRecord rec:copy)
			{
			boolean ok=false;
			for(List<SAMRecord> row:rows)
				{
				SAMRecord last=row.get(row.size()-1);
				if(right(last)+1 < left(rec))
					{
					row.add(rec);
					ok=true;
					break;
					}
				}
			if(!ok)
				{
				List<SAMRecord> row=new ArrayList<SAMRecord>(1);
				row.add(rec);
				rows.add(row);
				}
			}
		return rows;
		}
	}
