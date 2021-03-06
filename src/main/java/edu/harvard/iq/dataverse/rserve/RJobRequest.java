/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
package edu.harvard.iq.dataverse.rserve;

/**
 * original
 * @author Akio Sone (DVN 2.*)
 * @author Leonid Andreev
 */
import edu.harvard.iq.dataverse.datavariable.DataVariable;
import java.util.*;
import java.util.logging.*;
import org.apache.commons.lang.*;


public class RJobRequest {

    private static Logger dbgLog = Logger.getLogger(RJobRequest.class.getPackage().getName());


    private Map<String,String> variableFormats = new HashMap<String,String>(); 
    
    /**
     * 4 parameter Constructor:
     */

    public RJobRequest(
            List <DataVariable> dv, 
            Map <String, Map<String, String>> vts,
            Map <String, List<String>> categoryOrders
            ) {
        dataVariablesForRequest = dv;
                
        valueTables = vts;
        categoryValueOrders=categoryOrders;
        dbgLog.fine("***** DvnRJobRequest: within the default constructor : initial *****");
        dbgLog.fine("DvnRJobRequest: variables="+dataVariablesForRequest);
        dbgLog.fine("DvnRJobRequest: value table="+valueTables);
        dbgLog.fine("DvnRJobRequest: category value orders="+categoryValueOrders);
        
        
        checkVariableNames();
        
        dbgLog.fine("***** DvnRJobRequest: within the default constructor ends here *****");
    }

    
    public RJobRequest(
            List<DataVariable> dv, 
            Map <String, Map <String, String>> vts
            ) {
      this(dv,vts,null);
    }

    

    private List<DataVariable> dataVariablesForRequest;

    private String tabularDataFileName; 
    
    private String requestType;
    // Note: the only "request type" supported in 4.0 (as of currently
    // planned is "convert" - for converting tab files to data frames)
    
    private String formatRequested; 
    // Again, the plan is have "RData" as the only format supported; 
    // but we'll keep the mechanism in place for supporting multiple formats. 
    
    // R work space, saved and cached on the Application side
    private String savedRworkSpace;
    
    private Map<String, Map<String, String>> valueTables;
    
    /** list-type (one-to-many) parameter */
    private Map<String, List<String>> categoryValueOrders;
    
    public String[] safeVarNames = null;
    public String[] renamedVariableArray=null;
    public String[] renamedResultArray=null;
    public Map<String, String> raw2safeTable = null;
    
    public Map<String, String> safe2rawTable = null;

    public boolean hasUnsafeVariableNames = false;
    
    public void setRequestType (String requestType) {
        this.requestType = requestType; 
    }
    
    public String getRequestType() {
        return this.requestType;
    }

    public void setFormatRequested (String formatRequested) {
        this.formatRequested = formatRequested; 
    }
    
    public String getFormatRequested() {
        return this.formatRequested;
    }
    
    public void setTabularDataFileName(String tabularDataFileName) {
        this.tabularDataFileName = tabularDataFileName;
    }
    
    public String getTabularDataFileName() {
        return this.tabularDataFileName;
    }
    
    public List<DataVariable> getDataVariablesForRequest(){
        return this.dataVariablesForRequest;
    }
    
    public String getCachedRworkSpace(){
	return this.savedRworkSpace; 
    }


    /**
     * getVariableTypes()
     * @return    An arrary of variable types(0, 1, 2, 3)
     * (3 is for Boolean)
     */
    
    public int[] getVariableTypes() {
        
        List<Integer> rw = new ArrayList<Integer>();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
            if (!StringUtils.isEmpty(dv.getFormatCategory())){
                if (dv.getFormatCategory().toLowerCase().equals("date") ||
                    (dv.getFormatCategory().toLowerCase().equals("time"))){
                    rw.add(0);
                } else if (dv.getFormatCategory().equals("Boolean")) {
                    rw.add(3); 
                } else {
                    if (dv.getVariableFormatType().getId() == 1L) {
                        if (dv.getVariableIntervalType().getId() == null) {
                            rw.add(2);
                        } else {
                            if (dv.getVariableIntervalType().getId() == 2L) {
                                rw.add(2);
                            } else {
                                rw.add(1);
                            }
                        }
                    } else if (dv.getVariableFormatType().getId() == 2L) {
                        rw.add(0);
                    }
                }
            } else {
                if (dv.getVariableFormatType().getId() == 1L) {
                    if (dv.getVariableIntervalType().getId() == null) {
                        rw.add(2);
                    } else {
                        if (dv.getVariableIntervalType().getId() == 2L) {
                            rw.add(2);
                        } else {
                            rw.add(1);
                        }
                    }
                } else if (dv.getVariableFormatType().getId() == 2L) {
                    rw.add(0);
                }
            }
        }
        Integer[]tmp = (Integer[])rw.toArray(new Integer[rw.size()]);
        dbgLog.fine("vartype="+ StringUtils.join(tmp, ", "));
        int[] variableTypes=new int[tmp.length];
        for (int j=0;j<tmp.length;j++){
            variableTypes[j]= tmp[j];
        }
        return variableTypes;
    }
    
    /*
     * TODO: get rid of this? 
     * -- L.A. 4.0 alpha 1
    */
    
    public List<String> getVariableTypesAsString() {
        
        List<String> rw = new ArrayList<String>();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
            if (!StringUtils.isEmpty(dv.getFormatCategory())){
                if (dv.getFormatCategory().toLowerCase().equals("date") ||
                    dv.getFormatCategory().toLowerCase().equals("time")){
                    rw.add("0");
                } else {
                    if (dv.getVariableFormatType().getId() == 1L) {
                        if (dv.getVariableIntervalType().getId() == null) {
                            rw.add("2");
                        } else {
                            if (dv.getVariableIntervalType().getId() == 2L) {
                                rw.add("2");
                            } else {
                                rw.add("1");
                            }
                        }
                    } else if (dv.getVariableFormatType().getId() == 2L) {
                        rw.add("0");
                    }
                }
            } else {
                if (dv.getVariableFormatType().getId() == 1L) {
                    if (dv.getVariableIntervalType().getId() == null) {
                        rw.add("2");
                    } else {
                        if (dv.getVariableIntervalType().getId() == 2L) {
                            rw.add("2");
                        } else {
                            rw.add("1");
                        }
                    }
                } else if (dv.getVariableFormatType().getId() == 2L) {
                    rw.add("0");
                }
            }
        }
        return rw;
    }



    /**
     * Getter for property variable formats
     *
     * @return    A Map that maps a format to
     *            its corresponding type, either time or date
     */
    public Map<String, String> getVariableFormats() {
        Map<String, String> variableFormats=new LinkedHashMap<String, String>();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);

            dbgLog.fine(String.format("DvnRJobRequest: column[%d] schema = %s", i, dv.getFormatSchema()));
            dbgLog.fine(String.format("DvnRJobRequest: column[%d] category = %s", i, dv.getFormatCategory()));
            
            dbgLog.fine(i+"-th \tformatschema="+dv.getFormatSchema());
            dbgLog.fine(i+"-th \tformatcategory="+dv.getFormatCategory());
            
            if (!StringUtils.isEmpty(dv.getFormatCategory())) {
                if (dv.getFormatSchema().toLowerCase().equals("spss")){
                    if (dv.getFormatCategory().toLowerCase().equals("date")){
                        // add this var to this map value D
                        variableFormats.put(getSafeVariableName(dv.getName()), "D");
                    } else if (dv.getFormatCategory().toLowerCase().equals("time")){
                        // add this var to this map
                        if ( dv.getFormatSchemaName().toLowerCase().startsWith("dtime")){
                            // value JT
                            variableFormats.put(getSafeVariableName(dv.getName()), "JT");
                            
                        } else if ( dv.getFormatSchemaName().toLowerCase().startsWith("datetime")){
                            // value DT
                            variableFormats.put(getSafeVariableName(dv.getName()), "DT");
                        } else {
                            // value T
                            variableFormats.put(getSafeVariableName(dv.getName()), "T");
                        }
                    }
                }
                
                else if (dv.getFormatSchema().toLowerCase().equals("rdata")) {
                  if (dv.getFormatCategory().toLowerCase().equals("date")) {
                    variableFormats.put(getSafeVariableName(dv.getName()), "D");
                  }
                  else if (dv.getFormatCategory().toLowerCase().equals("time")) {
                    // add this var to this map
                    if ( dv.getFormatSchemaName().toLowerCase().startsWith("dtime")){
                      // value JT
                      variableFormats.put(getSafeVariableName(dv.getName()), "JT");
                    }
                    else if (dv.getFormatSchemaName().toLowerCase().startsWith("datetime")) {
                      // Set as date-time-timezone, DT
                      variableFormats.put(getSafeVariableName(dv.getName()), "DT");
                    }
                    else if (dv.getFormatSchemaName().toLowerCase().startsWith("time")) {
                      // Set as date-time-timezone, DT
                      variableFormats.put(getSafeVariableName(dv.getName()), "DT");
                    }
                    else {
                      // value T
                      variableFormats.put(getSafeVariableName(dv.getName()), "T");
                    }
                  }
                }
                else if (dv.getFormatSchema().toLowerCase().equals("other")) {
                  if (dv.getFormatCategory().toLowerCase().equals("date")) {
                    // value = D
                    variableFormats.put(getSafeVariableName(dv.getName()), "D");
                  }
                }
                // TODO: (?)
                // What about STATA? -- L.A.
            } else {
                dbgLog.fine(i+"\t var: not date or time variable");
            }
        }
        dbgLog.fine("format="+variableFormats);
        return variableFormats;
    }
        
    private String getSafeVariableName(String raw){
        String safe =null;
        if ((raw2safeTable == null) || (raw2safeTable.isEmpty())) {
            // use raw
            dbgLog.fine("no unsafe variables");
            safe = raw;
        } else {
            // check this var is unsafe
            
            if (raw2safeTable.containsKey(raw)){
                dbgLog.fine("this var is unsafe="+raw);
                safe = raw2safeTable.get(raw);
                dbgLog.fine("safe var is:"+ safe);
            } else {
                dbgLog.fine("not on the unsafe list");
                safe = raw;
            }
        }
        return safe;
    }
    
    public String[] getVariableNames() {
        String[] variableNames=null;
        
        List<String> rw = new ArrayList();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
                rw.add(dv.getName());
        }
        
        variableNames = (String[])rw.toArray(new String[rw.size()]);
        return variableNames;
    }
    
    /**
     * Getter for property raw-to-safe-variable-name list
     * @return    A Map that maps an unsafe variable name to 
     *            a safe one
     */
    public Map<String, String> getRaw2SafeVarNameTable(){
        return raw2safeTable;
    }

    public void checkVariableNames(){
        
        VariableNameCheckerForR nf = new VariableNameCheckerForR(getVariableNames());
        if (nf.hasRenamedVariables()){
             safeVarNames  = nf.getFilteredVarNames();
             hasUnsafeVariableNames = true;
        }
        
        raw2safeTable = nf.getRaw2safeTable();
        safe2rawTable = nf.getSafe2rawTable();
        renamedVariableArray = nf.getRenamedVariableArray();
        renamedResultArray   = nf.getRenamedResultArray();
    }
    
    public List<String> getFilteredVarNameSet(List<String> varIdSet){
        List<String> varNameSet = new ArrayList<String>();
        for (String vid : varIdSet){
            dbgLog.fine("name list: vid="+vid);
            String raw = getVarIdToRawVarNameTable().get(vid);
            if (raw != null){
                dbgLog.fine("raw is not null case="+raw);
                if (raw2safeTable.containsKey(raw)){
                    dbgLog.fine("raw is unsafe case");
                    varNameSet.add(raw2safeTable.get(raw));
                } else {
                    dbgLog.fine("raw is safe case");
                    varNameSet.add(raw);
                }
            } else {
                dbgLog.fine("raw is null-case");
            }
        }
        dbgLog.fine("varNameSet="+varNameSet);
        return varNameSet;
    }
    
    public String[] getVariableIds(){
        String[] variableIds=null;
        List<String> rw = new ArrayList();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
                rw.add("v"+dv.getId().toString());
        }
        
        variableIds = (String[])rw.toArray(new String[rw.size()]);
        return variableIds;
    }

    public Map<String, String> getVarIdToRawVarNameTable(){
        Map<String, String> vi2rwn = new HashMap<String, String>();
        
        for(DataVariable dv :dataVariablesForRequest){
            vi2rwn.put("v"+dv.getId(), dv.getName());
        }
        return vi2rwn;
    }

    public Map<String, String> getRawVarNameToVarIdTable(){
        Map<String, String> rwn2Id = new HashMap<String, String>();
        
        for(DataVariable dv :dataVariablesForRequest){
            rwn2Id.put(dv.getName(), "v"+dv.getId());
        }
        return rwn2Id;
    }

    public String[] getUpdatedVariableNames(){
        List<String> tmp = new ArrayList<String>();
        if (!hasUnsafeVariableNames){
            // neither renemaed nor recoded vars
            return  getVariableNames();
        } 
            
        return safeVarNames;
    }

    /**
     * Getter for property variable labels
     *
     * @return    A String array of variable labels
     */
    public String[] getVariableLabels(){
        String [] variableLabels=null;
        List<String> rw = new ArrayList();
        for(int i=0;i < dataVariablesForRequest.size(); i++){
            DataVariable dv = (DataVariable) dataVariablesForRequest.get(i);
                rw.add(dv.getLabel());
        }
        
        variableLabels = (String[])rw.toArray(new String[rw.size()]);
        return variableLabels;
    }

    public Map<String, Map<String,String>> getValueTable(){
        return valueTables;
    }
    
    public Map<String, List<String>> getCategoryValueOrders (){
      return this.categoryValueOrders;
    }
    /*
    public String[] getBaseVarIdSet(){
        List<String> bvid = listParametersForRequest.get("baseVarIdSet");
        String[] tmp = (String[])bvid.toArray(new String[bvid.size()]);
        return tmp;

    }
    
    public String[] getBaseVarNameSet(){
        List<String> bvn = listParametersForRequest.get("baseVarNameSet");
        String[] tmp = (String[])bvn.toArray(new String[bvn.size()]);
        return tmp;
    }
    */
    
    public String[] String2StringArray(String token) {
        char[] temp = token.toCharArray();
        String[] tmp = new String[temp.length];
        for (int i=0; i<temp.length; i++) {
           tmp[i] = String.valueOf(temp[i]);
        }
        return tmp;
    }

    
}
