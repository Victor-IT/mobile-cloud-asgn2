package org.magnum.mobilecloud.video.controller;

import org.magnum.mobilecloud.video.repository.Video;
import org.magnum.mobilecloud.video.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Set;

@Controller
public class VideoController {

    private static final String VIDEO_PATH = "/video";
    private static final String SEARCH_PATH = VIDEO_PATH + "/search";
    private final VideoRepository videoRepository;

    @Autowired
    public VideoController(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    @RequestMapping(value = VIDEO_PATH, method = RequestMethod.GET)
    @ResponseBody
    public Collection<Video> getVideoList() {
        return videoRepository.findAll();
    }

    @RequestMapping(value = VIDEO_PATH, method = RequestMethod.POST)
    @ResponseBody
    public Video addVideo(@RequestBody Video video) {
        return videoRepository.save(video);
    }

    @RequestMapping(value = SEARCH_PATH + "/findByName", method = RequestMethod.GET)
    @ResponseBody
    public Collection<Video> getVideoByName(@RequestParam String title) {
        return videoRepository.findAllByName(title);
    }

    @RequestMapping(value = VIDEO_PATH + "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Video getVideoById(@PathVariable Long id) {
        return videoRepository.findOne(id);
    }

    @RequestMapping(value = SEARCH_PATH + "/findByDurationLessThan", method = RequestMethod.GET)
    @ResponseBody
    public Collection<Video> getByDurationLessThan(@RequestParam Long duration) {
        return videoRepository.findByDurationLessThan(duration);
    }

    @RequestMapping(value = VIDEO_PATH + "/{id}/like", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity likeVideo(
            @RequestHeader(value = "Authorization") String token,
            @PathVariable Long id) {
        Video video = videoRepository.findOne(id);
        if (video == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Set<String> likedBy = video.getLikedBy();
        if (likedBy.contains(token)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        video.setLikes(video.getLikes() + 1);
        likedBy.add(token);
        video.setLikedBy(likedBy);
        videoRepository.save(video);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = VIDEO_PATH + "/{id}/unlike", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity unlikeVideo(
            @RequestHeader(value="Authorization") String token,
            @PathVariable Long id) {
        Video video = videoRepository.findOne(id);
        if (video == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Set<String> likedBy = video.getLikedBy();
        if (!likedBy.contains(token)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        video.setLikes(video.getLikes() - 1);
        likedBy.remove(token);
        video.setLikedBy(likedBy);
        videoRepository.save(video);

        return new ResponseEntity(HttpStatus.OK);
    }
}
