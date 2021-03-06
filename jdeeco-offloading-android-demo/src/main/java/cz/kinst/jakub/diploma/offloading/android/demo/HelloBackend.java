package cz.kinst.jakub.diploma.offloading.android.demo;


import org.restlet.representation.Representation;
import org.restlet.resource.Post;

import cz.kinst.jakub.diploma.offloading.backend.OffloadableBackend;

/**
 * Created by Jakub Kinst 2015
 * E-mail: jakub@kinst.cz
 */
public interface HelloBackend extends OffloadableBackend {

	@Post("?hello")
	public Message getHello(String name);

	@Post("?hi")
	public Message getHi(String name);

	@Post("?file")
	public Message testFile(Representation file);
}
