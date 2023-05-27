#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Created on Mon Sep 12 ... 2021
@author: ifly6
"""
import re
import time
from collections import namedtuple

import requests
import pandas as pd
from bs4 import BeautifulSoup

Post = namedtuple('Post', ['title', 'post', 'repealed', 'is_empty'])
url = 'https://forum.nationstates.net/viewtopic.php?f=9&t=30&start={}'

# create containers and helper functions for parsing
seen_list = []
titles_and_posts = []


def is_repealed(the_posttitle):
    post_text = str(the_posttitle.parent.parent)

    # this isn't always marked properly... but it should be dispositive if marked
    if '[REPEALED]' in the_posttitle.text:
        if '<del>' in post_text:
            return True
        else:
            # shouldn't ever happen, check for consistency!
            raise RuntimeError(
                f'post {i} marked as repealed but does not have strike-throughs!')

    # strikethrough is present only in repealed resolutions
    if '<del>' in post_text and '</del>' in post_text:
        if 'struck out by' in post_text.lower() or 'repealed by' in post_text.lower():
            return True

        else:
            # this also shouldn't ever happen, do manual check for consistency...
            raise RuntimeError(
                f'post {i} contains del tags but no "repealed by" tags!')

    return False


def is_empty(unused):
    post_text = post_title.parent.parent.select('div.content')[0].text.strip()

    # normal posts should never start with a full stop
    if post_text.startswith('.') or len(post_text) == 0:
        return True

    return False


# parse the raw forum data
no_more = False
for i in range(0, 40):
    if no_more is True:
        break

    adj_value = i * 25
    adj_url = url.format(adj_value)
    print(f'getting posts {i}', end=' ')

    soup = BeautifulSoup(requests.get(
        adj_url, headers={
            'User-Agent': 'imperium anglorum updating passed resolutions',
            'From': 'cyrilparsons.london@gmail.com'
        }).text, 'lxml')
    post_titles = soup.select('div.postbody h3 a')
    assert len(post_titles) > 0, 'no post titles to be found'

    for post_title in post_titles:
        if post_title['href'] in ['#p309', '#p310']:
            # don't attempt to parse the intro and table!
            continue

        if post_title['href'] in seen_list:
            no_more = True
            break

        try:
            text = post_title.parent.parent.select('div.content span')[0].text
        except IndexError:
            text = ''

        titles_and_posts.append(Post(
            text,
            re.search(r'\d+', post_title['href'])[0],
            is_repealed(post_title),
            is_empty(post_title)))
        seen_list.append(post_title['href'])

    print('...', end='\n')
    time.sleep(5)

# create data frame for tabular repr
posts = pd.DataFrame(titles_and_posts)

resolutions = posts.copy()  # manual exclude rexis intro and contents posts

# correct strange bolding
# regex MUST be any character; this includes stuff like `[Struck out by ABC]`
resolutions['title'] = resolutions['title'] \
    .str.strip() \
    .str.replace(r'\[.+\]$', '', regex=True) \
    .str.strip()


def title_capitalise(title: str) -> str:
    """
    Make a very good faith effort to capitalise everyone's names and all the GA resolution titles properly.
    This will of course fail on exceptions like `Foucaults garden` et al but it is intended that most GA resolutions
    will be properly capitalised.
    :type title: str
    :return the input string `title`, hopefully, capitalised
    """

    def __in_ignorecase(s, l):
        # return any(s.lower() == i.lower() for i in l)
        return s.lower() in map(str.lower, l)

    def __previous_element_in(l1, i, haystack):
        if i > len(l1) or i < 0:
            return False
        return l1[i - 1] in haystack

    def __next_element_in(l1, i, haystack):
        if i < 0 or i >= len(l1) - 1:
            return False
        return l1[i + 1] in haystack

    # # if the name is in ref, un ref it; ignore case
    # # if all(i.islower() for i in title) and ' ' not in title:  # doesn't work
    # if ref(title) == title.lower():
    #     title = un_ref(title)

    # capitalise all except exceptions, but only when exceptions is not the first element
    _EXCEPTIONS = {'a', 'an', 'of', 'the', 'is',
                   'and', 'for', 'to', 'on', 'etc', 'in', 'or'}
    _QUOTES = {'"', '\''}
    _APOSTROPHES = {'\'', '’'}
    tokens = [
        s for s in
        # word boundaries and quote signs
        re.split(r'\b|(?<=\s)|(?=\s)', title.lower())
        if s != ''  # but no empty strings wasting my memory
    ]
    tokens = [
        s if
        # don't capitalise if it's an exception that isn't the first element and isn't preceded by a quote
        (__in_ignorecase(s, _EXCEPTIONS) and i != 0 and not __previous_element_in(tokens, i, _QUOTES)) or
        # don't capitalise if the next element is an apostrophe and you are one letter long
        (__next_element_in(tokens, i, _APOSTROPHES) and len(s) == 1) or
        # don't capitalise if you are a single letter after an apostrophe
        (__previous_element_in(tokens, i, _APOSTROPHES) and len(s) == 1)
        else s.capitalize()
        for i, s in enumerate(tokens)
    ]

    # force upper case for literals and regex matches
    _FORCE_UPPER = {
        'AI', 'WA', 'GA', 'NSIA', 'GMO', 'LEO', 'STI', 'US', 'USA',  # acronyms
        'LGBT', 'LGBTQIA', 'LGBTIQA',  # other acronyms
        'TNP', 'TSP', 'TEP', 'TWP', 'TRR',  # regional names
        'II', 'III', 'IV', 'V', 'VI', 'VII', 'VIII', 'IX', 'X', 'XI', 'XII', 'XIII', 'XIV', 'XV', 'XVI',  # numerals
    }
    _FORCE_UPPER_REGEXES = {r'lgb[a-z]*', }
    tokens = [
        s.upper() if __in_ignorecase(s, _FORCE_UPPER) or any(
            re.match(i, s, flags=re.IGNORECASE) for i in _FORCE_UPPER_REGEXES
        ) else s
        for s in tokens
    ]

    return ''.join(tokens)


# correct title capitalisation errors; copied from proprietary infoeurope
resolutions['title'] = resolutions['title'].apply(title_capitalise)

# shift using original index to pages
assert resolutions['is_empty'].eq(True).sum() != 0, 'filtered too early'
resolutions['page'] = ((resolutions.index + 2) // 25) + 1

# get rid of the empty ones
resolutions = resolutions[~resolutions['is_empty']]

# reindex numbers starting from 1
resolutions['num'] = range(1, len(resolutions) + 1)

# ensure numbering is correct
for k, v in {
    'access to abortion': 499,
    'nuclear arms possession act': 10,
    'deposit insurance fund': 625,
    'ban on secret treaties': 408,
    'reproductive freedoms': 286,
    'the charter of civil rights': 35,  # need THE
    'on abortion': 128,
    'reducing statelessness': 386
}.items():
    row = resolutions[resolutions['title'].str.lower().eq(k.lower())].iloc[0]
    assert row['num'] == v

# format out code-form for post entry
resolutions['code'] = resolutions.apply(
    lambda r: '[post={}]GA {} \'{}\'[/post]'.format(
        r['post'], r['num'], r['title']), axis=1)  # make post bbcode

# create page boundaries then for each page, generate and append links
lines = ['[b][color=darkblue]Page 1[/color][/b]', '']
for g, df in resolutions.groupby('page'):
    if g != 1:
        lines.append('')
        lines.append(f'[b][color=darkblue]Page {g}[/b][/color]')
        lines.append('')
        # lines.append('')

    for i, r in df.iterrows():
        if not r['is_empty']:
            lines.append('' + r['code'] +  # used to be '[*]'
                         (' [b][REPEALED][/b]' if r['repealed'] else ''))

# save to var and print
contents = '\n'.join(lines)  # + '[/list]'
print(contents)

# print to file
resolutions.to_csv('post_resolutions.csv', index=True)
with open('post.txt', 'w') as f:
    f.write(contents)

assert 'Struck Out By Resolution' not in contents.lower()

# Nb! if using var explorer in spyder, open the string in that and copy it all for pasting!
