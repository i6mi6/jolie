/***************************************************************************
 *   Copyright (C) by Fabrizio Montesi                                     *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Library General Public License as       *
 *   published by the Free Software Foundation; either version 2 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 *                                                                         *
 *   You should have received a copy of the GNU Library General Public     *
 *   License along with this program; if not, write to the                 *
 *   Free Software Foundation, Inc.,                                       *
 *   59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.             *
 *                                                                         *
 *   For details about the authors of this software, see the AUTHORS file. *
 ***************************************************************************/

package joliex.util;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

import jolie.net.CommMessage;
import jolie.runtime.FaultException;
import jolie.runtime.JavaService;
import jolie.runtime.Value;
import jolie.runtime.ValueVector;

public class ExecService extends JavaService
{
	public CommMessage exec( CommMessage request )
		throws FaultException
	{
		Vector< String > command = new Vector< String >();
		String[] str = request.value().strValue().split( " " );
		for( int i = 0; i < str.length; i++ ) {
			command.add( str[i] );
		}

		for( Value v : request.value().getChildren( "args" ) ) {
			command.add( v.strValue() );
		}
		//String input = null;
		ProcessBuilder builder = new ProcessBuilder( command );
		//builder.redirectErrorStream( true );
		try {
			Value response = Value.create();
			Process p = builder.start();
			ValueVector waitFor = request.value().children().get( "waitFor" );
			if ( waitFor == null || waitFor.first().intValue() > 0 ) {
				int exitCode = p.waitFor();
				response.getNewChild( "exitCode" ).setValue( exitCode );
				int len = p.getInputStream().available();
				if ( len > 0 ) {
					char[] buffer = new char[ len ];
					BufferedReader reader = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
					reader.read( buffer, 0, len );
					response.setValue( new String( buffer ) );
				}
			}
			return CommMessage.createResponse( request, response );
		} catch( Exception e ) {
			throw new FaultException( e );
		}
	}
}
