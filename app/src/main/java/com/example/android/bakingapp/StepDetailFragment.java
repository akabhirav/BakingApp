package com.example.android.bakingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingapp.data.Step;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;


public class StepDetailFragment extends Fragment implements Player.EventListener {
    private static final String ARG_STEP = "step";
    private static final String STATE_CURRENT_PLAYER_POSITION = "current_player_position";
    private static final String STATE_STEP = "step";
    private static final String STATE_PLAYER_PLAYBACK_STATE = "current_playback_state";
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private ImageView mPlayerImageView;
    private Step mStep;

    private static final String TAG = StepDetailFragment.class.getName();
    private static MediaSessionCompat mMediaSession;
    private static PlaybackStateCompat.Builder mStateBuilder;

    public StepDetailFragment() {
    }

    public static StepDetailFragment create(Step step) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mStep = args.getParcelable(ARG_STEP);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        long currentPosition = 0;
        boolean currentPlaybackState = true;
        if (savedInstanceState != null) {
            mStep = savedInstanceState.getParcelable(STATE_STEP);
            currentPosition = savedInstanceState.getLong(STATE_CURRENT_PLAYER_POSITION, 0);
            currentPlaybackState = savedInstanceState.getBoolean(STATE_PLAYER_PLAYBACK_STATE, true);
        }
        final View rootView = inflater.inflate(R.layout.step_detail_fragment, container, false);
        mPlayerView = rootView.findViewById(R.id.playerView);
        mPlayerImageView = rootView.findViewById(R.id.playerImageView);
        TextView mStepDescriptionTextView = rootView.findViewById(R.id.tv_step_instruction);
        if (!mStep.getVideoURL().equals("")) {
            mPlayerView.setVisibility(View.VISIBLE);
            mPlayerImageView.setVisibility(View.GONE);
            initializeMediaSession();
            initializePlayer(Uri.parse(mStep.getVideoURL()), currentPosition, currentPlaybackState);
        } else if (!mStep.getThumbnailURL().equals("")) {
            mPlayerView.setVisibility(View.GONE);
            mPlayerImageView.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(Uri.parse(mStep.getThumbnailURL())).into(mPlayerImageView);
        } else {
            mPlayerView.setVisibility(View.GONE);
            mPlayerImageView.setVisibility(View.GONE);
        }
        if (mStepDescriptionTextView != null)
            mStepDescriptionTextView.setText(mStep.getDescription());
        return rootView;
    }

    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(getContext(), TAG);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_FAST_FORWARD |
                                PlaybackStateCompat.ACTION_REWIND);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new MySessionCallback());
        mMediaSession.setActive(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_STEP, mStep);
        if (mExoPlayer != null) {
            outState.putLong(STATE_CURRENT_PLAYER_POSITION, mExoPlayer.getCurrentPosition());
            outState.putBoolean(STATE_PLAYER_PLAYBACK_STATE, mExoPlayer.getPlayWhenReady());
        }
    }

    private void initializePlayer(Uri mediaUri, long currentPosition, boolean currentPlaybackState) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
            String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                    new DefaultDataSourceFactory(getContext(), userAgent),
                    new DefaultExtractorsFactory(),
                    null, null);
            mExoPlayer.prepare(mediaSource, false, true);
            mExoPlayer.seekTo(currentPosition);
            mExoPlayer.setPlayWhenReady(currentPlaybackState);
            mPlayerView.setPlayer(mExoPlayer);
        }
    }

    void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_READY && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mExoPlayer.getCurrentPosition(), 1f);
        } else if (playbackState == Player.STATE_READY) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onFastForward() {
            mExoPlayer.seekTo(mExoPlayer.getCurrentPosition() + 2000);
        }

        @Override
        public void onRewind() {
            mExoPlayer.seekTo(mExoPlayer.getCurrentPosition() - 2000);
        }
    }

    public static class StepDetailMediaReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
}
