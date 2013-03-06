package io.emmet;

public interface IUserData {
	public void load(Emmet ctx);
	public void loadExtensions(Emmet ctx);
	public String[] additionalSourceJS();
}
