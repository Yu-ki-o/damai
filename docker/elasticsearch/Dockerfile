FROM elasticsearch:8.5.2
RUN bin/elasticsearch-plugin install --batch https://get.infini.cloud/elasticsearch/analysis-ik/8.5.2
COPY --chown=elasticsearch:root users users_roles /usr/share/elasticsearch/config/
