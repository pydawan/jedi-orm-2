/***********************************************************************************************
 * @(#)CountryTest.java
 * 
 * Version: 1.0
 * 
 * Date: 2014/08/06
 * 
 * Copyright (c) 2014 Thiago Alexandre Martins Monteiro.
 * 
 * All rights reserved. This program and the accompanying materials are made 
 * available under the terms of the GNU Public License v2.0 which accompanies 
 * this distribution, and is available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *    Thiago Alexandre Martins Monteiro - initial API and implementation
 ************************************************************************************************/

package jedi.tests.unittests;

import jedi.db.engine.JediORMEngine;
import jedi.db.models.QuerySet;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import app.models.Country;

public class CountryTest {

	@BeforeClass
    public static void testSetup() {
    	JediORMEngine.FOREIGN_KEY_CHECKS = false;
    	JediORMEngine.flush();
        QuerySet<Country> countries = new QuerySet<>();
        // Adds countries.
        countries.add(new Country("Albania", "AL"));
        countries.add(new Country("Armenia", "AM"));
        countries.add(new Country("Netherlands Antilles", "AN"));
        countries.add(new Country("Angola", "AO"));
        countries.add(new Country("Antarctica", "AQ"));
        countries.add(new Country("Argentina", "AR"));
        countries.add(new Country("American Samoa", "AS"));
        countries.add(new Country("Austria", "AT"));
        countries.add(new Country("Australia", "AU"));
        countries.add(new Country("Aruba", "AW"));
        countries.add(new Country("Åland Islands", "AX"));
        countries.add(new Country("Azerbaijan", "AZ"));
        countries.add(new Country("Bosnia and Herzegovina", "BA"));
        countries.add(new Country("Barbados", "BB"));
        countries.add(new Country("Bangladesh", "BD"));
        countries.add(new Country("Belgium", "BE"));
        countries.add(new Country("Burkina Faso", "BF"));
        countries.add(new Country("Bulgaria", "BG"));
        countries.add(new Country("Bahrain", "BH"));
        countries.add(new Country("Burundi", "BI"));
        countries.add(new Country("Benin", "BJ"));
        countries.add(new Country("Saint Barthélemy", "BL"));
        countries.add(new Country("Bermuda", "BM"));
        countries.add(new Country("Brunei Darussalam", "BN"));
        countries.add(new Country("Bolivia, Plurinational State of", "BO"));
         countries.add(new Country("Brazil", "BR") );
        countries.add(new Country("Bahamas", "BS"));
        countries.add(new Country("Bhutan", "BT"));
        countries.add(new Country("Bouvet Island", "BV"));
        countries.add(new Country("Botswana", "BW"));
        countries.add(new Country("Belarus", "BY"));
        countries.add(new Country("Belize", "BZ"));
        countries.add(new Country("Canada", "CA"));
        countries.add(new Country("Cocos (Keeling) Islands", "CC"));
        countries.add(new Country("Congo, the Democratic Republic of the", "CD"));
        countries.add(new Country("Central African Republic", "CF"));
        countries.add(new Country("Congo", "CG"));
        countries.add(new Country("Switzerland", "CH"));
        countries.add(new Country("Côte d'Ivoire", "CI"));
        countries.add(new Country("Cook Islands", "CK"));
        countries.add(new Country("Chile", "CL"));
        countries.add(new Country("Cameroon", "CM"));
        countries.add(new Country("China", "CN"));
        countries.add(new Country("Colombia", "CO"));
        countries.add(new Country("Costa Rica", "CR"));
        countries.add(new Country("Cuba", "CU"));
        countries.add(new Country("Cape Verde", "CV"));
        countries.add(new Country("Christmas Island", "CX"));
        countries.add(new Country("Cyprus", "CY"));
        countries.add(new Country("Czech Republic", "CZ"));
        countries.add(new Country("Germany", "DE"));
        countries.add(new Country("Djibouti", "DJ"));
        countries.add(new Country("Denmark", "DK"));
        countries.add(new Country("Dominica", "DM"));
        countries.add(new Country("Dominican Republic", "DO"));
        countries.add(new Country("Algeria", "DZ"));
        countries.add(new Country("Ecuador", "EC"));
        countries.add(new Country("Estonia", "EE"));
        countries.add(new Country("Egypt", "EG"));
        countries.add(new Country("Western Sahara", "EH"));
        countries.add(new Country("Eritrea", "ER"));
        countries.add(new Country("Spain", "ES"));
        countries.add(new Country("Ethiopia", "ET"));
        countries.add(new Country("Finland", "FI"));
        countries.add(new Country("Fiji", "FJ"));
        countries.add(new Country("Falkland Islands (Malvinas)", "FK"));
        countries.add(new Country("Micronesia, Federated States of", "FM"));
        countries.add(new Country("Faroe Islands", "FO"));
        countries.add(new Country("France", "FR"));
        countries.add(new Country("Gabon", "GA"));
        countries.add(new Country("United Kingdom", "GB"));
        countries.add(new Country("Grenada", "GD"));
        countries.add(new Country("Georgia", "GE"));
        countries.add(new Country("French Guiana", "GF"));
        countries.add(new Country("Guernsey", "GG"));
        countries.add(new Country("Ghana", "GH"));
        countries.add(new Country("Gibraltar", "GI"));
        countries.add(new Country("Greenland", "GL"));
        countries.add(new Country("Gambia", "GM"));
        countries.add(new Country("Guinea", "GN"));
        countries.add(new Country("Guadeloupe", "GP"));
        countries.add(new Country("Equatorial Guinea", "GQ"));
        countries.add(new Country("Greece", "GR"));
        countries.add(new Country("South Georgia and the South Sandwich Islands", "GS"));
        countries.add(new Country("Guatemala", "GT"));
        countries.add(new Country("Guam", "GU"));
        countries.add(new Country("Guinea-Bissau", "GW"));
        countries.add(new Country("Guyana", "GY"));
        countries.add(new Country("Hong Kong", "HK"));
        countries.add(new Country("Heard Island and McDonald Islands", "HM"));
        countries.add(new Country("Honduras", "HN"));
        countries.add(new Country("Croatia", "HR"));
        countries.add(new Country("Haiti", "HT"));
        countries.add(new Country("Hungary", "HU"));
        countries.add(new Country("Indonesia", "ID"));
        countries.add(new Country("Ireland", "IE"));
        countries.add(new Country("Israel", "IL"));
        countries.add(new Country("Isle of Man", "IM"));
        countries.add(new Country("India", "IN"));
        countries.add(new Country("British Indian Ocean Territory", "IO"));
        countries.add(new Country("Iraq", "IQ"));
        countries.add(new Country("Iran, Islamic Republic of", "IR"));
        countries.add(new Country("Iceland", "IS"));
        countries.add(new Country("Italy", "IT"));
        countries.add(new Country("Jersey", "JE"));
        countries.add(new Country("Jamaica", "JM"));
        countries.add(new Country("Jordan", "JO"));
        countries.add(new Country("Japan", "JP"));
        countries.add(new Country("Kenya", "KE"));
        countries.add(new Country("Kyrgyzstan", "KG"));
        countries.add(new Country("Cambodia", "KH"));
        countries.add(new Country("Kiribati", "KI"));
        countries.add(new Country("Comoros", "KM"));
        countries.add(new Country("Saint Kitts and Nevis", "KN"));
        countries.add(new Country("Korea, Democratic People's Republic of", "KP"));
        countries.add(new Country("Korea, Republic of", "KR"));
        countries.add(new Country("Kuwait", "KW"));
        countries.add(new Country("Cayman Islands", "KY"));
        countries.add(new Country("Kazakhstan", "KZ"));
        countries.add(new Country("Lao People's Democratic Republic", "LA"));
        countries.add(new Country("Lebanon", "LB"));
        countries.add(new Country("Saint Lucia", "LC"));
        countries.add(new Country("Liechtenstein", "LI"));
        countries.add(new Country("Sri Lanka", "LK"));
        countries.add(new Country("Liberia", "LR"));
        countries.add(new Country("Lesotho", "LS"));
        countries.add(new Country("Lithuania", "LT"));
        countries.add(new Country("Luxembourg", "LU"));
        countries.add(new Country("Latvia", "LV"));
        countries.add(new Country("Libyan Arab Jamahiriya", "LY"));
        countries.add(new Country("Morocco", "MA"));
        countries.add(new Country("Monaco", "MC"));
        countries.add(new Country("Moldova, Republic of", "MD"));
        countries.add(new Country("Montenegro", "ME"));
        countries.add(new Country("Saint Martin (French part)", "MF"));
        countries.add(new Country("Madagascar", "MG"));
        countries.add(new Country("Marshall Islands", "MH"));
        countries.add(new Country("Macedonia, the former Yugoslav Republic of", "MK"));
        countries.add(new Country("Mali", "ML"));
        countries.add(new Country("Myanmar", "MM"));
        countries.add(new Country("Mongolia", "MN"));
        countries.add(new Country("Macao", "MO"));
        countries.add(new Country("Northern Mariana Islands", "MP"));
        countries.add(new Country("Martinique", "MQ"));
        countries.add(new Country("Mauritania", "MR"));
        countries.add(new Country("Montserrat", "MS"));
        countries.add(new Country("Malta", "MT"));
        countries.add(new Country("Mauritius", "MU"));
        countries.add(new Country("Maldives", "MV"));
        countries.add(new Country("Malawi", "MW"));
        countries.add(new Country("Mexico", "MX"));
        countries.add(new Country("Malaysia", "MY"));
        countries.add(new Country("Mozambique", "MZ"));
        countries.add(new Country("Namibia", "NA"));
        countries.add(new Country("New Caledonia", "NC"));
        countries.add(new Country("Niger", "NE"));
        countries.add(new Country("Norfolk Island", "NF"));
        countries.add(new Country("Nigeria", "NG"));
        countries.add(new Country("Nicaragua", "NI"));
        countries.add(new Country("Netherlands", "NL"));
        countries.add(new Country("Norway", "NO"));
        countries.add(new Country("Nepal", "NP"));
        countries.add(new Country("Nauru", "NR"));
        countries.add(new Country("Niue", "NU"));
        countries.add(new Country("New Zealand", "NZ"));
        countries.add(new Country("Oman", "OM"));
        countries.add(new Country("Panama", "PA"));
        countries.add(new Country("Peru", "PE"));
        countries.add(new Country("French Polynesia", "PF"));
        countries.add(new Country("Papua New Guinea", "PG"));
        countries.add(new Country("Philippines", "PH"));
        countries.add(new Country("Pakistan", "PK"));
        countries.add(new Country("Poland", "PL"));
        countries.add(new Country("Saint Pierre and Miquelon", "PM"));
        countries.add(new Country("Pitcairn", "PN"));
        countries.add(new Country("Puerto Rico", "PR"));
        countries.add(new Country("Palestinian Territory, Occupied", "PS"));
        countries.add(new Country("Portugal", "PT"));
        countries.add(new Country("Palau", "PW"));
        countries.add(new Country("Paraguay", "PY"));
        countries.add(new Country("Qatar", "QA"));
        countries.add(new Country("Réunion", "RE"));
        countries.add(new Country("Romania", "RO"));
        countries.add(new Country("Serbia", "RS"));
        countries.add(new Country("Russian Federation", "RU"));
        countries.add(new Country("Rwanda", "RW"));
        countries.add(new Country("Saudi Arabia", "SA"));
        countries.add(new Country("Solomon Islands", "SB"));
        countries.add(new Country("Seychelles", "SC"));
        countries.add(new Country("Sudan", "SD"));
        countries.add(new Country("Sweden", "SE"));
        countries.add(new Country("Singapore", "SG"));
        countries.add(new Country("Saint Helena", "SH"));
        countries.add(new Country("Slovenia", "SI"));
        countries.add(new Country("Svalbard and Jan Mayen", "SJ"));
        countries.add(new Country("Slovakia", "SK"));
        countries.add(new Country("Sierra Leone", "SL"));
        countries.add(new Country("San Marino", "SM"));
        countries.add(new Country("Senegal", "SN"));
        countries.add(new Country("Somalia", "SO"));
        countries.add(new Country("Suriname", "SR"));
        countries.add(new Country("Sao Tome and Principe", "ST"));
        countries.add(new Country("El Salvador", "SV"));
        countries.add(new Country("Syrian Arab Republic", "SY"));
        countries.add(new Country("Swaziland", "SZ"));
        countries.add(new Country("Turks and Caicos Islands", "TC"));
        countries.add(new Country("Chad", "TD"));
        countries.add(new Country("French Southern Territories", "TF"));
        countries.add(new Country("Togo", "TG"));
        countries.add(new Country("Thailand", "TH"));
        countries.add(new Country("Tajikistan", "TJ"));
        countries.add(new Country("Tokelau", "TK"));
        countries.add(new Country("Timor-Leste", "TL"));
        countries.add(new Country("Turkmenistan", "TM"));
        countries.add(new Country("Tunisia", "TN"));
        countries.add(new Country("Tonga", "TO"));
        countries.add(new Country("Turkey", "TR"));
        countries.add(new Country("Trinidad and Tobago", "TT"));
        countries.add(new Country("Tuvalu", "TV"));
        countries.add(new Country("Taiwan, Province of China", "TW"));
        countries.add(new Country("Tanzania, United Republic of", "TZ"));
        countries.add(new Country("Ukraine", "UA"));
        countries.add(new Country("Uganda", "UG"));
        countries.add(new Country("United States Minor Outlying Islands", "UM"));
        countries.add(new Country("United States", "US") );
        countries.add(new Country("Uruguay", "UY"));
        countries.add(new Country("Uzbekistan", "UZ"));
        countries.add(new Country("Holy See (Vatican City State)", "VA"));
        countries.add(new Country("Saint Vincent and the Grenadines", "VC"));
        countries.add(new Country("Venezuela, Bolivarian Republic of", "VE"));
        countries.add(new Country("Virgin Islands, British", "VG"));
        countries.add(new Country("Virgin Islands, U.S.", "VI"));
        countries.add(new Country("Viet Nam", "VN"));
        countries.add(new Country("Vanuatu ", "VU"));
        countries.add(new Country("Wallis and Futuna", "WF"));
        countries.add(new Country("Samoa", "WS"));
        countries.add(new Country("Yemen", "YE"));
        countries.add(new Country("Mayotte", "YT"));
        countries.add(new Country("South Africa", "ZA"));
        countries.add(new Country("Zambia", "ZM"));
        countries.add(new Country("Zimbabwe", "ZW"));
        countries.save();
    }
	
    @AfterClass
    public static void testCleanup() {
        // Country.objects.all().delete();
    	JediORMEngine.droptables();
    }

    @Test
    public void testInsert() {
        // Expected.
        Country expectedCountry = new Country("Brazil", "BR");
        expectedCountry.insert();
        // Obtained.
        Country obtainedCountry = Country.objects.get("name", "Brazil");
        // Assertion.
        Assert.assertEquals(expectedCountry.getId(), obtainedCountry.getId());
    }

    @Test
    public void testUpdate() {
        // Expected.
        Country expectedCountry = Country.objects.get("name", "Brazil");
        expectedCountry.update("name='Brazil'");
        // Obtained.
        Country obtainedCountry = Country.objects.get("acronym", "BR");
        // Assertion.
        Assert.assertTrue(expectedCountry.getName().equals(obtainedCountry.getName()));
    }

    @Test
    public void testDelete() {
        // Expected.
        int expectedOcurrences = 0;
        // Iterates through a list of countries.
        for (Country country : Country.objects.<Country> all()) {
            country.delete();
        }
        // Obtained.
        int obtainedOcurrences = Country.objects.count();
        // Assertion.
        Assert.assertEquals(expectedOcurrences, obtainedOcurrences);
    }

    @Test
    public void testSaveInsert() {
        // Expected.
        Country expectedCountry = new Country();
        expectedCountry.setName("United States of America");
        expectedCountry.setAcronym("UU");
        expectedCountry.save();
        // Obtained.
        Country obtainedCountry = Country.objects.get("acronym", "UU");
        // Assertion.
        Assert.assertEquals(expectedCountry.getId(), obtainedCountry.getId());
    }

    @Test
    public void testSaveUpdate() {
        // Expected.
        Country expectedCountry = Country.objects.get("acronym", "UU");
        expectedCountry.setAcronym("US");
        expectedCountry.save();
        // Obtained.
        Country obtainedCountry = Country.objects.get("acronym", "US");
        // Assertion.
        Assert.assertTrue(expectedCountry.getName().equals(obtainedCountry.getName()));
    }
}