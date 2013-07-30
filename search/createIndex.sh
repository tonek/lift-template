#!/bin/sh
#curl http://localhost:9200/feeds/feed/_search?pretty=true -d '{query:{query_string:{query:"kotlin"}}, sort:[{creationDate:"desc",_score:"desc"}]}'
#curl -XDELETE 'http://localhost:9200/feeds/'
curl -XPUT 'http://localhost:9200/feeds' -d '{settings:{number_of_shards:1,number_of_replicas:0}}'