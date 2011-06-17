
CREATE TABLE IF NOT EXISTS `categories` (
  `id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

CREATE TABLE IF NOT EXISTS `categories_words` (
  `word_id` int(10) unsigned NOT NULL,
  `category_id` varchar(50) NOT NULL,
  `occurrences` int(10) unsigned NOT NULL,
  PRIMARY KEY (`word_id`,`category_id`),
  KEY `category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `documents` (
  `id` varchar(50) NOT NULL,
  `category_id` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `category_id` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ;

CREATE TABLE IF NOT EXISTS `documents_words` (
  `word_id` int(10) unsigned NOT NULL,
  `document_id` varchar(50) NOT NULL,
  `occurrences` int(10) unsigned NOT NULL,
  PRIMARY KEY (`word_id`,`document_id`),
  KEY `document_id` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `words` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;


ALTER TABLE `categories_words`
  ADD CONSTRAINT `categories_words_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `categories_words_ibfk_1` FOREIGN KEY (`word_id`) REFERENCES `words` (`id`) ON DELETE CASCADE;

ALTER TABLE `documents`
  ADD CONSTRAINT `documents_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `documents_words`
  ADD CONSTRAINT `documents_words_ibfk_2` FOREIGN KEY (`document_id`) REFERENCES `documents` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `documents_words_ibfk_1` FOREIGN KEY (`word_id`) REFERENCES `words` (`id`) ON DELETE CASCADE;
