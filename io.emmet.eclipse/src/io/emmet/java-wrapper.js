/**
 * Short-hand functions for Java wrapper
 * @author Sergey Chikuyonok (serge.che@gmail.com)
 * @link http://chikuyonok.ru
 */

function require(name) {
	return emmet.require(name);
}

/**
 * Runs Emmet action
 * @param {IEmmetEditor} editor
 * @param {String} actionName
 * @return {Boolean}
 */
function runEmmetAction(editor, actionName){
	var args = [editor];
	for (var i = 2, il = arguments.length; i < il; i++) {
		args.push(arguments[i]);
	}
	
	return require('actions').run(actionName, args);
}

function tryBoolean(val) {
	var strVal = String(val || '').toLowerCase();
	if (strVal == 'true')
		return true;
	if (strVal == 'false')
		return false;
		
	var intVal = parseInt(strVal, 10);
	if (!isNaN(intVal))
		return intVal;
	
	return strVal;
}

function previewWrapWithAbbreviation(editor, abbr) {
	abbr = String(abbr);
	if (!abbr)
		return null;
	
	var editorUtils = require('editorUtils');
	var utils = require('utils');
	var info = editorUtils.outputInfo(editor);
	
	var range = editor.getSelectionRange(),
		startOffset = range.start,
		endOffset = range.end;
		
	if (startOffset == endOffset) {
		// no selection, find tag pair
		range = require('html_matcher')(info.content, startOffset, info.profile);
		
		if (!range || range[0] == -1) // nothing to wrap
			return null;
		
		var narrowedSel = utils.narrowToNonSpace(info.content, range[0], range[1] - range[0]);
		startOffset = narrowedSel.start;
		endOffset = narrowedSel.end;
	}
	
	var newContent = utils.escapeText(info.content.substring(startOffset, endOffset));
	return require('wrapWithAbbreviation').wrap(abbr, editorUtils.unindent(editor, newContent), info.syntax, info.profile) 
		|| null;
}

function strToJSON(data) {
	try {
		return (new Function('return ' + String(data)))();
	} catch(e) {
		log('Error while parsing JSON: ' + e);
		return {};
	}
}

function javaLoadSystemSnippets(data) {
	if (data) {
		require('resources').setVocabulary(strToJSON(data), 'system');
	}
}

function javaLoadUserData(payload) {
	payload = strToJSON(payload);
	var profileMap = {
		'tagCase': 'tag_case',
		'attrCase': 'attr_case',
		'attrQuotes': 'attr_quotes',
		'tagNewline': 'tag_nl',
		'placeCaret': 'place_cursor',
		'indentTags': 'indent',
		'inlineBreak': 'inline_break',
		'selfClosing': 'self_closing_tag',
		'filters': 'filters'
	};

	var validPayload = {};

	// prepare profiles
	if ('profiles' in payload) {
		validPayload.syntaxProfiles = {};
		_.each(payload.profiles, function(profile, syntax) {
			var p = {};
			_.each(profile, function(v, k) {
				p[profileMap[k]] = v;
			});

			validPayload.syntaxProfiles[syntax] = p;
		});
	}

	// prepare snippets
	var snippets = {};
	var addSnippets = function(data, type) {
		if (!data) 
			return;

		_.each(data, function(item) {
			if (!(item[0] in snippets)) {
				snippets[item[0]] = {};
			}

			var syntaxCtx = snippets[item[0]];
			if (!syntaxCtx[type]) {
				syntaxCtx[type] = {};
			}

			syntaxCtx[type][item[1]] = item[2];
		});
	};

	addSnippets(payload.snippets, 'snippets');
	addSnippets(payload.abbreviations, 'abbreviations');
	validPayload.snippets = snippets;

	// prepare variables
	if ('variables' in payload) {
		validPayload.variables = {};
		_.each(payload.variables, function(item) {
			validPayload.variables[item[1]] = item[2];
		});
	}

	require('bootstrap').loadUserData(validPayload);
}

function javaLoadExtensions(payload) {
	require('bootstrap').loadExtensions(strToJSON(payload));
}

function javaExtractTabstops(text) {
	return require('tabStops').extract(text, {
		escape: function(ch) {
			return ch;
		}
	});
}

function log(message) {
	java.lang.System.out.println('JS: ' + message);
}