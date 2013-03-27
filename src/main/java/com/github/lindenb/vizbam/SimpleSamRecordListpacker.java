package com.github.lindenb.vizbam;

import java.util.ArrayList;
import java.util.List;

import net.sf.samtools.SAMRecord;

public class SimpleSamRecordListpacker extends AbstractSamRecordListpacker
	{
	public SimpleSamRecordListpacker()
		{
		}
	@Override
	public List<List<SAMRecord>> pack(final List<SAMRecord> records) {
		List<SAMRecord> copy=new ArrayList<SAMRecord>(records);
		sortSamRecords(copy);
		List<List<SAMRecord>> rows=new ArrayList<List<SAMRecord>>(copy.size());
		for(SAMRecord rec:copy)
			{
			List<SAMRecord> row=new ArrayList<SAMRecord>(1);
			row.add(rec);
			rows.add(row);
			}
		return rows;
		}
	}
