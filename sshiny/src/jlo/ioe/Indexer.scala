package jlo.ioe

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.IndexWriter

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Date
import jlo.ioe.data._

object IndexObject {
  val INDEX_DIR = new File("db/index");

  def indexObject(o : DataObject) = {

  }
  
  /** Index all text files under a directory. */
  def main(args : Array[String]) = {
    if (INDEX_DIR.exists()) {
      System.out.println("Cannot save index to '" +INDEX_DIR+ "' directory, please delete it first");
      System.exit(1);
    }
    
    val docDir = new File(args(0));
    if (!docDir.exists() || !docDir.canRead()) {
      System.out.println("Document directory '" +docDir.getAbsolutePath()+ "' does not exist or is not readable, please check the path");
      System.exit(1);
    }
    
    val start = new Date();
    val writer = new IndexWriter(INDEX_DIR, new StandardAnalyzer(), true);
    System.out.println("Indexing to directory '" +INDEX_DIR+ "'...");
    indexDocs(writer, docDir);
    System.out.println("Optimizing...");
    writer.optimize();
    writer.close();
    
    val end = new Date();
    System.out.println(end.getTime() - start.getTime() + " total milliseconds");

  }

  def indexDocs(writer: IndexWriter, file : File) : Unit = {
    // do not try to index files that cannot be read
    if (file.canRead()) {
      if (file.isDirectory()) {
        val files = file.list();
        // an IO error could occur
        if (files != null) {
          for (val f <- files) {
            indexDocs(writer, new File(file, f));
          }
        }
      } else {
        System.out.println("adding " + file);
        //writer.addDocument(FileDocument.Document(file));
      }
    }
  }
}
