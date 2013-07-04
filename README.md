# ring-gzip-middleware [![Build Status](https://secure.travis-ci.org/rm-hull/ring-gzip-middleware.png)](http://travis-ci.org/rm-hull/ring-gzip-middleware)


Gzips [Ring](http://github.com/mmcgrana/ring) responses for user agents which can handle it.

## Usage

Apply the Ring middleware function `ring.middleware.gzip/wrap-gzip` to
your Ring handler, typically at the top level (i.e. as the last bit of
middleware in a `->` form).


## Installation

Add `[rm-hull/ring-gzip-middleware "0.1.6"]` to your Leingingen dependencies.

## License

Copyright (C) 2010 Michael Stephens and other contributors.

Distributed under an MIT-style license (see LICENSE for details).
