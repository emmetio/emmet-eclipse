package io.emmet.eclipse;

import io.emmet.actions.AbstractMenuItem;
import io.emmet.actions.Action;
import io.emmet.actions.Menu;
import io.emmet.eclipse.handlers.DefaultAction;
import io.emmet.eclipse.handlers.ExpandAbbreviationAction;
import io.emmet.eclipse.handlers.WrapWithAbbreviationAction;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;


public class MainMenuContribution extends ExtensionContributionFactory {

	@Override
	public void createContributionItems(IServiceLocator serviceLocator,
			final IContributionRoot additions) {
		
		Menu rootMenu = Menu.create();
		for (AbstractMenuItem item : rootMenu.getItems()) {
			additions.addContributionItem(createContributionItem(serviceLocator, item), null);
		}
	}
	
	private IContributionItem createContributionItem(IServiceLocator serviceLocator, AbstractMenuItem item) {
		if (item instanceof Menu)
			return createContributionItem(serviceLocator, (Menu) item);
		
		return createContributionItem(serviceLocator, (Action) item);
	}
	
	private IContributionItem createContributionItem(IServiceLocator serviceLocator, Action item) {
		ICommandService commandService = (ICommandService) serviceLocator.getService(ICommandService.class);
		
		Command command = commandService.getCommand("io.emmet.eclipse.commands." + item.getId());
		if (!command.isDefined()) {
			command.define(item.getName(), "", commandService.getCategory("io.emmet.eclipse.commands.category"));
		}
		
		IHandlerService handlerService = (IHandlerService) serviceLocator.getService(IHandlerService.class); 
		handlerService.activateHandler(command.getId(), handlerFactory(item.getId()));
		
		CommandContributionItemParameter p = new CommandContributionItemParameter(
				serviceLocator, "", command.getId(), CommandContributionItem.STYLE_PUSH);
		
		p.label = item.getName();
		
		CommandContributionItem contribItem = new CommandContributionItem(p);
		contribItem.setVisible(true);
		
		return contribItem;
	}
	
	private IContributionItem createContributionItem(IServiceLocator serviceLocator, Menu item) {
		MenuManager submenu = new MenuManager(item.getName(), null);
		for (AbstractMenuItem subitem : item.getItems()) {
			submenu.add(createContributionItem(serviceLocator, subitem));
		}
		
		return submenu;
	}
	
	private AbstractHandler handlerFactory(String actionId) {
		if (actionId.equals("expand_abbreviation"))
			return new ExpandAbbreviationAction();
		
		if (actionId.equals("wrap_with_abbreviation"))
			return new WrapWithAbbreviationAction();
		
		return new DefaultAction();
	}

}
