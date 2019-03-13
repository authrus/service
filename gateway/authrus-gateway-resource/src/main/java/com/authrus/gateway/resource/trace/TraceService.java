package com.authrus.gateway.resource.trace;

import static com.authrus.http.proxy.trace.search.Progress.ACTIVE;
import static com.authrus.http.proxy.trace.search.Progress.BOTH;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import com.authrus.common.time.DateTime;
import com.authrus.common.time.DateTime.Duration;
import com.authrus.gateway.deploy.trace.TraceContext;
import com.authrus.http.proxy.trace.TraceEvent;
import com.authrus.http.proxy.trace.search.AddressQuery;
import com.authrus.http.proxy.trace.search.And;
import com.authrus.http.proxy.trace.search.EventFormatter;
import com.authrus.http.proxy.trace.search.EventQuery;
import com.authrus.http.proxy.trace.search.IdentityQuery;
import com.authrus.http.proxy.trace.search.ProgressQuery;
import com.authrus.http.proxy.trace.search.Query;
import com.authrus.http.proxy.trace.search.SearchRecorder;
import com.authrus.http.proxy.trace.search.SearchResult;
import com.authrus.http.proxy.trace.search.TimeElapsedQuery;
import com.authrus.http.proxy.trace.search.ValueQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TraceService {

   private static final String DATE_PATTERN = "HH:mm:ss";
   private static final int RESULT_LIMIT = 1000;

   private final TraceContext context;

   public List<TraceMatch> findAll(TraceCategory category, String name) {
      Query query = new ProgressQuery(BOTH);

      return search(category, name, query);
   }

   public List<TraceMatch> findActive(TraceCategory category, String name) {
      Query query = new ProgressQuery(ACTIVE);

      return search(category, name, query);
   }

   public List<TraceMatch> findAll(TraceCategory category, String name, long seconds) {
      Query progress = new ProgressQuery(BOTH);
      Query time = new TimeElapsedQuery(seconds, SECONDS);
      Query query = new And(progress, time);

      return search(category, name, query);
   }

   public List<TraceMatch> findActive(TraceCategory category, String name, long seconds) {
      Query progress = new ProgressQuery(ACTIVE);
      Query time = new TimeElapsedQuery(seconds, SECONDS);
      Query query = new And(progress, time);

      return search(category, name, query);
   }

   public List<TraceMatch> findAllById(TraceCategory category, String name, String id) {
      Query progress = new ProgressQuery(BOTH);
      Query match = new IdentityQuery(id);
      Query and = new And(progress, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findActiveById(TraceCategory category, String name, String id) {
      Query progress = new ProgressQuery(ACTIVE);
      Query match = new IdentityQuery(id);
      Query and = new And(progress, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findAllByValue(TraceCategory category, String name, String pattern) {
      Query progress = new ProgressQuery(BOTH);
      Query match = new ValueQuery(pattern);
      Query and = new And(progress, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findActiveByValue(TraceCategory category, String name, String pattern) {
      Query progress = new ProgressQuery(ACTIVE);
      Query match = new ValueQuery(pattern);
      Query and = new And(progress, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findAllByValue(TraceCategory category, String name, String pattern, long seconds) {
      Query progress = new ProgressQuery(BOTH);
      Query match = new ValueQuery(pattern);
      Query time = new TimeElapsedQuery(seconds, SECONDS);
      Query and = new And(progress, time, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findActiveByValue(TraceCategory category, String name, String pattern, long seconds) {
      Query progress = new ProgressQuery(ACTIVE);
      Query match = new ValueQuery(pattern);
      Query time = new TimeElapsedQuery(seconds, SECONDS);
      Query and = new And(progress, time, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findAllByEvent(TraceCategory category, String name, String pattern) {
      Query progress = new ProgressQuery(BOTH);
      Query match = new EventQuery(pattern);
      Query and = new And(progress, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findActiveByEvent(TraceCategory category, String name, String pattern) {
      Query progress = new ProgressQuery(ACTIVE);
      Query match = new EventQuery(pattern);
      Query and = new And(progress, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findAllByEvent(TraceCategory category, String name, String pattern, long seconds) {
      Query progress = new ProgressQuery(BOTH);
      Query match = new EventQuery(pattern);
      Query time = new TimeElapsedQuery(seconds, SECONDS);
      Query and = new And(progress, time, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findActiveByEvent(TraceCategory category, String name, String pattern, long seconds) {
      Query progress = new ProgressQuery(ACTIVE);
      Query match = new EventQuery(pattern);
      Query time = new TimeElapsedQuery(seconds, SECONDS);
      Query and = new And(progress, time, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findAllByAddress(TraceCategory category, String name, String pattern) {
      Query progress = new ProgressQuery(BOTH);
      Query match = new AddressQuery(pattern);
      Query and = new And(progress, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findActiveByAddress(TraceCategory category, String name, String pattern) {
      Query progress = new ProgressQuery(ACTIVE);
      Query match = new AddressQuery(pattern);
      Query and = new And(progress, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findAllByAddress(TraceCategory category, String name, String pattern, long seconds) {
      Query progress = new ProgressQuery(BOTH);
      Query match = new AddressQuery(pattern);
      Query time = new TimeElapsedQuery(seconds, SECONDS);
      Query and = new And(progress, time, match);

      return search(category, name, and);
   }

   public List<TraceMatch> findActiveByAddress(TraceCategory category, String name, String pattern, long seconds) {
      Query progress = new ProgressQuery(ACTIVE);
      Query match = new AddressQuery(pattern);
      Query time = new TimeElapsedQuery(seconds, SECONDS);
      Query and = new And(progress, time, match);

      return search(category, name, and);
   }

   public void clear(TraceCategory category, String name) {
      SearchRecorder recorder = category.getRecorder(context, name);

      if(recorder != null) {
         recorder.clear();
      }
   }

   public List<TraceMatch> search(TraceCategory category, String name, Query query) {
      List<TraceMatch> matches = new ArrayList<>();
      EventFormatter formatter = new EventFormatter();

      if(query != null) {
         SearchRecorder recorder = category.getRecorder(context, name);

         if(recorder != null) {
            SortedSet<SearchResult> results = recorder.find(query);

            for (SearchResult result : results) {
               Collection<TraceEvent> events = result.getEvents();
               TraceEvent previous = result.getFirstEvent();
               long startMillis = previous.getTime();
               long id = result.getId();
               int size = matches.size();

               if (size > RESULT_LIMIT) {
                  return Collections.unmodifiableList(matches);
               }
               List<TraceEntry> entries = new ArrayList<>();

               for (TraceEvent event : events) {
                  String thread = event.getThread();
                  Object value = event.getValue();
                  Object type = event.getEvent();
                  String text = formatter.format(value);
                  DateTime timeStamp = event.getDateTime();
                  Duration timeElapsed = timeStamp.timeElapsed();
                  String time = timeStamp.formatDate(DATE_PATTERN);
                  long currentMillis = event.getTime();
                  long previousMillis = previous.getTime();
                  long duration = currentMillis - previousMillis;
                  long total = currentMillis - startMillis;
                  TraceEntry entry = TraceEntry.builder()
                          .id(id)
                          .thread(thread)
                          .eventDuration(duration)
                          .totalDuration(total)
                          .event(String.valueOf(type))
                          .elapsed(String.valueOf(timeElapsed))
                          .message(text)
                          .time(time)
                          .build();

                  entries.add(entry);
                  previous = event;
               }
               TraceMatch group = TraceMatch.builder()
                       .entries(entries)
                       .category(category)
                       .name(name)
                       .id(id)
                       .build();

               matches.add(group);
            }
         }
      }
      return Collections.unmodifiableList(matches);
   }
}


