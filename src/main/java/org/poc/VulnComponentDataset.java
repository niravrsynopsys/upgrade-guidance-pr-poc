package org.poc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VulnComponentDataset {
    public static JSONObject generateVulnComponentDataset(JSONObject inputJsonObj) throws JSONException {

        // Create result object template
        JSONObject result = new JSONObject();
        result.put("CRITICAL", new JSONObject());
        result.put("HIGH", new JSONObject());
        result.put("MEDIUM", new JSONObject());
        result.put("LOW", new JSONObject());

        JSONArray items = inputJsonObj.getJSONArray("items");

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);

            // Direct dependency is identified by the transitiveUpgradeGuidance array being empty
            Boolean isDirect = item.getJSONArray("transitiveUpgradeGuidance").length() == 0;

            JSONObject newItem = new JSONObject();
            newItem.put("externalId", item.getString("externalId"));
            newItem.put("shortTermUpgradeGuidance", item.getJSONObject("shortTermUpgradeGuidance"));
            newItem.put("longTermUpgradeGuidance", item.getJSONObject("longTermUpgradeGuidance"));
            if (isDirect) {
                newItem.put("filePath", "");
                newItem.put("lineNumber", "");
            }

            JSONArray vulnerabilities = item.getJSONArray("allVulnerabilities");
            for (int j = 0; j < vulnerabilities.length(); j++) {

                JSONObject currVulnerability = vulnerabilities.getJSONObject(j);
                String currVulnName = currVulnerability.getString("name");
                String currVulnSeverity = currVulnerability.getString("vulnSeverity");
                String currVulnDescription = currVulnerability.getString("description");

                // Fetch the vulnerability object based on it's key i.e. vulnName
                JSONObject vulnObject;
                if (result.getJSONObject(currVulnSeverity).has(currVulnName)) {
                    vulnObject = result.getJSONObject(currVulnSeverity).getJSONObject(currVulnName);
                }
                // If not found, create a new template object
                else {
                    JSONObject newVulnObject = new JSONObject();
                    JSONObject newVulnDependencies = new JSONObject();
                    newVulnDependencies.put("directDependencies", new JSONArray());
                    newVulnDependencies.put("transitiveDependencies", new JSONArray());
                    newVulnObject.put("vulnDependencies", newVulnDependencies);
                    vulnObject = newVulnObject;
                }

                // Update the vulnerability object
                vulnObject.put("vulnDescription", currVulnDescription);
                if (isDirect) {
                    JSONArray directDepArray = vulnObject.getJSONObject("vulnDependencies").getJSONArray("directDependencies");
                    directDepArray.put(newItem);
                } else {
                    JSONArray transitiveDepArray = vulnObject.getJSONObject("vulnDependencies").getJSONArray("transitiveDependencies");
                    transitiveDepArray.put(newItem);
                }

                result.getJSONObject(currVulnSeverity).put(currVulnName, vulnObject);
            }
        }

        return result;
    }
}
