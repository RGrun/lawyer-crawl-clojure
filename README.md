# lawyer-crawl

Scrapes personal injury lawyer data from Cornell's lawyer listings site.

This small crawler will grab the professional info for every personal injury lawyer listed on Cornell's site.

## Why does this exist?

One day my boss asked me to write a webcrawler to go and scrape all the names, phone numbers, websites and emails
I could from Cornell University's lawyer listings site (https://lawyers.law.cornell.edu/lawyers/injury-accident-law/).

I wanted to use Clojure for something so this was my first attempt at it. It's pretty slow because it simply prints
output to a file (output.txt). I was thinking of speeding it up by crawling each state's lawyers in a separate thread,
but that would require setting up a database table. I was about to but my boss told me to just do it with Scrapy
in Python instead, and that's what we ended up using.

## Usage

In the project's root folder type "lein run" (you'll need to use Leiningen) to start the crawl.
The crawl's output will be in a file called "output.txt" when the crawl is finished.

If you only want to crawl specific states, modify the states vector to only contain the ones you want.

You could also change the type of lawyer you want to grab info for by modifying the URL that the crawler points at.
Cornell's site uses a standard format in their URLs (at least as of October 2015), so grabbing the kind of lawyer
you want should be a simple change.

## License

Do whatever you want with it.